package com.castellanos94.fuzzylogic.api.controller;

import com.castellanos94.fuzzylogic.api.db.QueryRepository;
import com.castellanos94.fuzzylogic.api.model.ResponseModel;
import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
public class QueryController {
    @Autowired
    QueryRepository queryRepository;
    @Autowired
    AsynchronousService service;


    @RequestMapping(value = "run", method = RequestMethod.POST, produces = {"application/json"})
    public ResponseEntity<ResponseModel> callRunner() {
        service.executeAsynchronously();
        return ResponseEntity.ok(new ResponseModel().setStatus(ResponseModel.Status.Running));
    }

    @RequestMapping(value = "evaluation", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<List<EvaluationQuery>> getEvaluations() {
        List<EvaluationQuery> queries = new ArrayList<>();
        queryRepository.findAll().stream().filter(q -> q instanceof EvaluationQuery).map(q -> ((EvaluationQuery) q)).forEachOrdered(queries::add);
        if (queries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(queries);
    }

    @RequestMapping(value = "evaluation", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<ResponseModel> upload(@RequestBody @Valid EvaluationQuery evaluationQuery) {
        StringBuilder msgBuilder = new StringBuilder();
        evaluationQuery.getStates().stream().filter(s -> s.getF() == null).forEachOrdered(linguisticState -> msgBuilder.append(linguisticState.getLabel()).append(", "));
        ResponseModel responseModel = new ResponseModel();
        if (msgBuilder.length() > 0) {
            responseModel.setStatus(ResponseModel.Status.Failed);
            String msg = msgBuilder.toString();
            msg = msg.substring(0, msg.lastIndexOf(","));
            responseModel.setMsg("The following linguistic states have no membership function: " + msg);
            return ResponseEntity.badRequest().body(responseModel);
        }
        System.out.println("Save in repository...");
        if (evaluationQuery.getId() != null) {
            evaluationQuery.setId(null);
        }
        EvaluationQuery save = queryRepository.save(evaluationQuery);
        return ResponseEntity.ok(responseModel.setStatus(ResponseModel.Status.Created).setId(save.getId()));
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
                responseModel.setStatus(ResponseModel.Status.Failed);
                String msg = msgBuilder.toString();
                msg = msg.substring(0, msg.lastIndexOf(","));
                responseModel.setMsg("The following generators are invalid: " + msg);
                return ResponseEntity.badRequest().body(responseModel);
            }
        }

        System.out.println("Save in repository...");
        if (discoveryQuery.getId() != null) {
            discoveryQuery.setId(null);
        }
        DiscoveryQuery save = queryRepository.save(discoveryQuery);

        return ResponseEntity.ok(responseModel.setStatus(ResponseModel.Status.Created).setId(save.getId()));
    }

    @RequestMapping(value = "discovery", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<List<DiscoveryQuery>> getDiscoveries() {
        List<DiscoveryQuery> queries = new ArrayList<>();
        queryRepository.findAll().stream().filter(q -> q instanceof DiscoveryQuery).map(q -> ((DiscoveryQuery) q)).forEachOrdered(queries::add);
        if (queries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(queries);
    }


    @RequestMapping(value = "dataset", method = RequestMethod.POST, consumes = {"multipart/form-data"}, produces = {"application/json"})
    public ResponseEntity<ResponseModel> uploadFile(@RequestParam("file") @Valid @NotNull @NotBlank MultipartFile file) {
        ResponseModel responseModel = new ResponseModel();

        if (!Utils.isCSVFile(file)) {
            responseModel.setStatus(ResponseModel.Status.Failed);
            responseModel.setMsg("Only CSV files are supported");
            return ResponseEntity.badRequest().body(responseModel);
        }
        FileUploadController.printFileDetails(file);
        responseModel.setStatus(ResponseModel.Status.Created);
        responseModel.setId("1213214A");
        return ResponseEntity.ok(responseModel);

    }
}
