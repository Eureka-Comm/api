package com.castellanos94.fuzzylogic.api.controller;

import com.castellanos94.fuzzylogic.api.db.EurekaTask;
import com.castellanos94.fuzzylogic.api.db.EurekaTaskRepository;
import com.castellanos94.fuzzylogic.api.db.FileUtils;
import com.castellanos94.fuzzylogic.api.model.ResponseModel;
import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import com.castellanos94.fuzzylogic.api.security.jwt.JwtUtils;
import com.castellanos94.fuzzylogic.api.security.services.UserDetailsImpl;
import com.castellanos94.fuzzylogic.api.service.AsynchronousService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            return ResponseEntity.ok(new ResponseModel().setStatus(task.getStatus()).setMsg(task.getMsg()).setId(task.getId()));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "evaluation", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> getEvaluations(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "3") int size) {
        getUserId();
        List<EvaluationQuery> queries = new ArrayList<>();
        long skipN = (long) page * size;
        eurekaTaskRepository.findAll().stream().filter(q -> q.getQuery() instanceof EvaluationQuery && !(q.getQuery() instanceof DiscoveryQuery)).map(q -> ((EvaluationQuery) q.getQuery())).skip(skipN).limit(size).forEachOrdered(queries::add);

        Map<String, Object> response = new HashMap<>();
        response.put("queries", queries);
        response.put("currentPage", page);

        long total = eurekaTaskRepository.findAll().stream().filter(q -> q.getQuery() instanceof EvaluationQuery && !(q.getQuery() instanceof DiscoveryQuery)).map(q -> ((EvaluationQuery) q.getQuery())).count();
        response.put("totalItems", total);
        long totalP = (long) Math.ceil(total / ((double) size));
        response.put("totalPages", (totalP < 1) ? 1 : totalP);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null) {
            if(authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                return userDetails.getId();
            }else if(authentication.getPrincipal() instanceof String){
                logger.error("anonymous user {}",authentication.getPrincipal());
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
    @RequestMapping(value = "discovery", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> getDiscoveries(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "3") int size) {
        List<DiscoveryQuery> queries = new ArrayList<>();
        long skipN = (long) page * size;
        eurekaTaskRepository.findAll().stream().filter(q -> q.getQuery() instanceof DiscoveryQuery).map(q -> ((DiscoveryQuery) q.getQuery())).skip(skipN).limit(size).forEachOrdered(queries::add);

        Map<String, Object> response = new HashMap<>();
        response.put("queries", queries);
        response.put("currentPage", page);
        response.put("totalItems", queries.size());
        long total = eurekaTaskRepository.findAll().stream().filter(q -> q.getQuery() instanceof DiscoveryQuery).map(q -> ((DiscoveryQuery) q.getQuery())).count();
        response.put("totalItems", total);
        long totalP = (long) Math.ceil(total / ((double) size));

        response.put("totalPages", (totalP < 1) ? 1 : totalP);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(security = {@SecurityRequirement(name = "ApiKey")})
    @RequestMapping(value = "result/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity dowloadResult(@PathVariable String id) throws IOException {
        Optional<EurekaTask> task = eurekaTaskRepository.findById(id);
        if (task.isPresent()) {
            File file = FileUtils.GET_OUTPUT_FILE(id);
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
    public ResponseEntity downloadDataset(@PathVariable String id) throws IOException {
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
