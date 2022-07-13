package com.castellanos94.fuzzylogic.api.controller;

import com.castellanos94.fuzzylogic.api.db.*;
import com.castellanos94.fuzzylogic.api.model.Query;
import com.castellanos94.fuzzylogic.api.model.ResponseModel;
import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import com.castellanos94.fuzzylogic.api.security.jwt.JwtUtils;
import com.castellanos94.fuzzylogic.api.security.services.UserDetailsImpl;
import com.castellanos94.fuzzylogic.api.service.AsynchronousService;
import com.castellanos94.fuzzylogic.api.utils.FileUtils;
import com.castellanos94.fuzzylogic.api.utils.ResultTaskUtils;
import com.castellanos94.fuzzylogicgp.core.ResultTask;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@SecurityScheme(
        name = "ApiKey", // can be set to anything
        type = SecuritySchemeType.HTTP,
        scheme = "bearer"
)

public class QueryController {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    EurekaTaskRepository eurekaTaskRepository;

    @Autowired
    ResultWrapperRepository resultWrapperRepository;
    @Autowired
    AsynchronousService service;

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "query/{id}", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<EurekaTask> getQuery(@PathVariable String id) {
        Optional<EurekaTask> optionalEurekaTask = eurekaTaskRepository.findById(id);
        return optionalEurekaTask.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "query/status/{id}", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<ResponseModel> getQueryStatus(@PathVariable String id) {
        Optional<EurekaTask> optionalEurekaTask = eurekaTaskRepository.findById(id);
        if (optionalEurekaTask.isPresent()) {
            EurekaTask task = optionalEurekaTask.get();
            ResponseModel model = new ResponseModel().setStatus(task.getStatus()).setMsg(task.getMsg()).setId(task.getId());
            if (task.getQuery() instanceof DiscoveryQuery && task.getStatus() == EurekaTask.Status.Running) {
                logger.error("entro aqui");
                model.setLog(service.getLog(task));
            }
            return ResponseEntity.ok(model);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "evaluation", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> getEvaluations(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "3") int size) {
        getUserId();
        Pageable paging = PageRequest.of(page, size);

        Page<EurekaTask> queryPage = eurekaTaskRepository.findByTaskType(EvaluationQuery.class.getName(), paging);
        Map<String, Object> response = new HashMap<>();
        response.put("queries", queryPage.getContent());
        response.put("currentPage", queryPage.getNumber());
        response.put("totalItems", queryPage.getTotalElements());
        response.put("totalPages", queryPage.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                return userDetails.getId();
            } else if (authentication.getPrincipal() instanceof String) {
                logger.error("anonymous user {}", authentication.getPrincipal());
            }
        }
        return null;
    }

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "evaluation", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    @Validated
    public ResponseEntity<ResponseModel> upload(@RequestBody @Valid EvaluationQuery evaluationQuery) {
        StringBuilder msgBuilder = new StringBuilder();
        evaluationQuery.getStates().stream().filter(s -> s.getF() == null || (s.getF() != null && !s.getF().isValid()))
                .forEachOrdered(linguisticState -> msgBuilder.append(linguisticState.getLabel()).append(", "));

        ResponseModel responseModel = new ResponseModel();
        if (msgBuilder.length() > 0) {
            responseModel.setStatus(EurekaTask.Status.Failed);
            String msg = msgBuilder.toString();
            msg = msg.substring(0, msg.lastIndexOf(","));
            responseModel.setMsg("The following linguistic states have no membership function: " + msg);
            return ResponseEntity.badRequest().body(responseModel);
        }
        logger.info("Save in repository...");

        EurekaTask save = eurekaTaskRepository.save(new EurekaTask().setQuery(evaluationQuery).setUserId(getUserId()));
        return ResponseEntity.ok(responseModel.setStatus(save.getStatus()).setId(save.getId()));
    }

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "discovery", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<ResponseModel> uploadDiscovery(@RequestBody @Valid DiscoveryQuery discoveryQuery) {
        ResponseModel responseModel = new ResponseModel();

        if (discoveryQuery.getGenerators() != null) {
            StringBuilder msgBuilder = new StringBuilder();
            discoveryQuery.getGenerators().forEach(g -> {
                if (!Utils.isValidGenerator(g)) {
                    msgBuilder.append(g.getLabel()).append(", ");
                }
            });
            if (msgBuilder.length() > 0) {
                responseModel.setStatus(EurekaTask.Status.Failed);
                String msg = msgBuilder.toString();
                msg = msg.substring(0, msg.lastIndexOf(","));
                responseModel.setMsg("The following generators are invalid: " + msg);
                return ResponseEntity.badRequest().body(responseModel);
            }
        }

        logger.info("Save in repository...");

        EurekaTask save = eurekaTaskRepository.save(new EurekaTask().setQuery(discoveryQuery).setUserId(getUserId()));

        return ResponseEntity.ok(responseModel.setStatus(save.getStatus()).setId(save.getId()));
    }

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "query", method = RequestMethod.POST, consumes = {"multipart/form-data"}, produces = {"application/json"})
    public ResponseEntity<ResponseModel> uploadQuery(@RequestPart @Valid Query query, @RequestPart("file") @Valid @NotNull @NotBlank MultipartFile file) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        if (query instanceof DiscoveryQuery) {
            DiscoveryQuery discoveryQuery = (DiscoveryQuery) query;
            if (discoveryQuery.getGenerators() != null) {
                StringBuilder msgBuilder = new StringBuilder();
                discoveryQuery.getGenerators().forEach(g -> {
                    if (!Utils.isValidGenerator(g)) {
                        msgBuilder.append(g.getLabel()).append(", ");
                    }
                });
                if (msgBuilder.length() > 0) {
                    responseModel.setStatus(EurekaTask.Status.Failed);
                    String msg = msgBuilder.toString();
                    msg = msg.substring(0, msg.lastIndexOf(","));
                    responseModel.setMsg("The following generators are invalid: " + msg);
                    return ResponseEntity.badRequest().body(responseModel);
                }
            }
        } else if (query instanceof EvaluationQuery) {
            EvaluationQuery evaluationQuery = (EvaluationQuery) query;
            StringBuilder msgBuilder = new StringBuilder();
            evaluationQuery.getStates().stream().filter(s -> s.getF() == null || (s.getF() != null && !s.getF().isValid()))
                    .forEachOrdered(linguisticState -> msgBuilder.append(linguisticState.getLabel()).append(", "));
            if (msgBuilder.length() > 0) {
                responseModel.setStatus(EurekaTask.Status.Failed);
                String msg = msgBuilder.toString();
                msg = msg.substring(0, msg.lastIndexOf(","));
                responseModel.setMsg("The following linguistic states have no membership function: " + msg);
                return ResponseEntity.badRequest().body(responseModel);
            }
        } else {
            responseModel.setStatus(EurekaTask.Status.Failed);
            responseModel.setMsg("Unsupported query");
            return ResponseEntity.badRequest().body(responseModel);
        }


        logger.info("Save in repository...");

        EurekaTask save = eurekaTaskRepository.save(new EurekaTask().setQuery(query).setUserId(getUserId()));

        String id = save.getId();
        logger.info("Validating file ...");
        if (!Utils.isCSVFile(file)) {
            responseModel.setStatus(EurekaTask.Status.Failed);
            responseModel.setMsg("Only CSV files are supported");
            return ResponseEntity.badRequest().body(responseModel);
        }
        if (FileUtils.SAVE_DATASET(id, file.getInputStream())) {
            responseModel.setStatus(EurekaTask.Status.Created);
            responseModel.setMsg("Temporarily saved datasets, task queued for execution.");
            service.executeAsynchronously(save);
        } else {
            responseModel.setStatus(EurekaTask.Status.Failed);
            responseModel.setMsg("Error saving the file");
        }
        return ResponseEntity.ok(responseModel.setStatus(save.getStatus()).setId(save.getId()));
    }

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "discovery", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> getDiscoveries(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "3") int size) {
        Pageable paging = PageRequest.of(page, size);

        Page<EurekaTask> queryPage = eurekaTaskRepository.findByTaskType(DiscoveryQuery.class.getName(), paging);


        Map<String, Object> response = new HashMap<>();
        response.put("queries", queryPage.getContent());
        response.put("currentPage", queryPage.getNumber());

        response.put("totalItems", queryPage.getTotalElements());
        response.put("totalPages", queryPage.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "result/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity downloadResult(@PathVariable String id, HttpServletRequest request) throws IOException {
        Optional<EurekaTask> task = eurekaTaskRepository.findById(id);
        ResultTask resultTask = null;
        if (task.isPresent()) {
            EurekaTask t = task.get();
            if (t.getStatus() == EurekaTask.Status.Done) {
                Optional<ResultWrapper> r = resultWrapperRepository.findByTaskIdFilter(id);
                if (r.isPresent()) {
                    resultTask = r.get().getResult();
                    String contentMediaType = request.getHeader("accept");
                    logger.error("Accept {}", contentMediaType);
                    if (contentMediaType.equalsIgnoreCase(MediaType.APPLICATION_OCTET_STREAM_VALUE) || contentMediaType.contains("*/*")) {
                        File file = FileUtils.GET_OUTPUT_FILE(t.getId());
                        if (resultTask != null)
                            ResultTaskUtils.export(file, resultTask);
                        if (file.exists()) {
                            return ResponseEntity.ok()
                                    .header("Content-Disposition", "attachment; filename=" + file.getName())
                                    .contentLength(file.length())
                                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                    .body(new FileSystemResource(file));
                        }
                    } else if (contentMediaType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
                        if (resultTask != null)
                            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resultTask);
                    } else {
                        return ResponseEntity.badRequest().body("Unsupported content-type");
                    }
                }
            }
            logger.error("Result task ? {}", resultTask);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "dataset/{id}", method = RequestMethod.POST, consumes = {"multipart/form-data"}, produces = {"application/json"})
    public ResponseEntity<ResponseModel> uploadFile(@PathVariable String
                                                            id, @RequestParam("file") @Valid @NotNull @NotBlank MultipartFile file) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        Optional<EurekaTask> task = eurekaTaskRepository.findById(id);
        if (task.isPresent()) {
            if (!Utils.isCSVFile(file)) {
                responseModel.setStatus(EurekaTask.Status.Failed);
                responseModel.setMsg("Only CSV files are supported");
                return ResponseEntity.badRequest().body(responseModel);
            }
            if (FileUtils.SAVE_DATASET(id, file.getInputStream())) {
                responseModel.setStatus(EurekaTask.Status.Created);
                responseModel.setMsg("Temporarily saved datasets, task queued for execution.");
                service.executeAsynchronously(task.get());
            } else {
                responseModel.setStatus(EurekaTask.Status.Failed);
                responseModel.setMsg("Error saving the file");
            }
            responseModel.setId(id);
            return ResponseEntity.ok(responseModel);
        } else {
            return ResponseEntity.badRequest().body(new ResponseModel().setMsg("Id not found"));
        }
    }

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "dataset/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity downloadDataset(@PathVariable String id) {
        Optional<EurekaTask> task = eurekaTaskRepository.findById(id);
        if (task.isPresent()) {
            File file = FileUtils.GET_DATASET_FILE(id);
            if (file.exists()) {
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=" + file.getName())
                        .contentLength(file.length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(new FileSystemResource(file));
            }
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
