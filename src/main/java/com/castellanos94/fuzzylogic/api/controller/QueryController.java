package com.castellanos94.fuzzylogic.api.controller;

import com.castellanos94.fuzzylogic.api.db.EurekaTask;
import com.castellanos94.fuzzylogic.api.db.EurekaTaskRepository;
import com.castellanos94.fuzzylogic.api.db.FileUtils;
import com.castellanos94.fuzzylogic.api.model.Query;
import com.castellanos94.fuzzylogic.api.model.ResponseModel;
import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import com.castellanos94.fuzzylogic.api.service.AsynchronousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
public class QueryController {
    @Autowired
    EurekaTaskRepository eurekaTaskRepository;
    @Autowired
    AsynchronousService service;


    @RequestMapping(value = "query/{id}", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<EurekaTask> getQuery(@PathVariable String id) {
        Optional<EurekaTask> optionalEurekaTask = eurekaTaskRepository.findById(id);
        return optionalEurekaTask.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @RequestMapping(value = "query/status/{id}", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<ResponseModel> getQueryStatus(@PathVariable String id) {
        Optional<EurekaTask> optionalEurekaTask = eurekaTaskRepository.findById(id);
        if (optionalEurekaTask.isPresent()) {
            EurekaTask task = optionalEurekaTask.get();
            return ResponseEntity.ok(new ResponseModel().setStatus(task.getStatus()).setMsg(task.getMsg()).setId(task.getId()));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "evaluation", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> getEvaluations(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "3") int size) {
        List<EvaluationQuery> queries = new ArrayList<>();
        long skipN = (long) page * size;
        eurekaTaskRepository.findAll().stream().filter(q -> q.getQuery() instanceof EvaluationQuery && !(q.getQuery() instanceof DiscoveryQuery)).map(q -> ((EvaluationQuery) q.getQuery())).skip(skipN).limit(size).forEachOrdered(queries::add);

        Map<String, Object> response = new HashMap<>();
        response.put("queries", queries);
        response.put("currentPage", page);
        response.put("totalItems", queries.size());
        long total = eurekaTaskRepository.findAll().stream().filter(q -> q.getQuery() instanceof EvaluationQuery && !(q.getQuery() instanceof DiscoveryQuery)).map(q -> ((EvaluationQuery) q.getQuery())).count() / size;
        response.put("totalPages", (total < 1) ? 1 : total);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "evaluation", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<ResponseModel> upload(@RequestBody @Valid EvaluationQuery evaluationQuery) {
        StringBuilder msgBuilder = new StringBuilder();
        evaluationQuery.getStates().stream().filter(s -> s.getF() == null).forEachOrdered(linguisticState -> msgBuilder.append(linguisticState.getLabel()).append(", "));
        ResponseModel responseModel = new ResponseModel();
        if (msgBuilder.length() > 0) {
            responseModel.setStatus(EurekaTask.Status.Failed);
            String msg = msgBuilder.toString();
            msg = msg.substring(0, msg.lastIndexOf(","));
            responseModel.setMsg("The following linguistic states have no membership function: " + msg);
            return ResponseEntity.badRequest().body(responseModel);
        }
        System.out.println("Save in repository...");

        EurekaTask save = eurekaTaskRepository.save(new EurekaTask().setQuery(evaluationQuery));
        return ResponseEntity.ok(responseModel.setStatus(save.getStatus()).setId(save.getId()));
    }

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

        System.out.println("Save in repository...");

        EurekaTask save = eurekaTaskRepository.save(new EurekaTask().setQuery(discoveryQuery));

        return ResponseEntity.ok(responseModel.setStatus(save.getStatus()).setId(save.getId()));
    }

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
        long total = eurekaTaskRepository.findAll().stream().filter(q -> q.getQuery() instanceof DiscoveryQuery).map(q -> ((DiscoveryQuery) q.getQuery())).count() / size;
        response.put("totalPages", (total < 1) ? 1 : total);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "result/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity dowloadResult(@PathVariable String id, HttpServletResponse response) throws IOException {
        Optional<EurekaTask> task = eurekaTaskRepository.findById(id);
        if (task.isPresent()) {
            File file = FileUtils.GET_OUTPUT_FILE(id);
            if (file.exists()) {
                Files.copy(file.toPath(), response.getOutputStream());
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
}
