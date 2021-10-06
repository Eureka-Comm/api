package com.castellanos94.fuzzylogic.api.controller;

import com.castellanos94.fuzzylogic.api.db.EurekaTaskRepository;
import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("public")
@Tag(name = "Info", description = "public information")
public class InfoController {
    @Autowired
    AsynchronousService service;
    @Autowired
    EurekaTaskRepository queryRepository;

    @Operation(summary = "Show active tasks", description = "Displays the number of currently active and waiting tasks")
    @RequestMapping(value = "server", method = RequestMethod.GET, produces = {"text/plain;charset=UTF-8"})
    public ResponseEntity<String> getRunning() {
        return ResponseEntity.ok(String.format("Active: %d, Queue: %d", service.getActiveThreads(), service.getQueueSize()));
    }

    @Operation(summary = "Public evaluation queries", description = "Return all public evaluation queries")
    @RequestMapping(value = "evaluation", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> getAllPublicEvaluations(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "3") int size) {
        List<EvaluationQuery> queries = new ArrayList<>();
        long skipN = (long) page * size;
        queryRepository.findAll().stream().filter(q -> q.getQuery() instanceof EvaluationQuery && !(q.getQuery() instanceof DiscoveryQuery) && q.getQuery().isPublic()).map(q -> ((EvaluationQuery) q.getQuery())).skip(skipN).limit(size).forEachOrdered(queries::add);

        Map<String, Object> response = new HashMap<>();
        response.put("queries", queries);
        response.put("currentPage", page);
        response.put("totalItems", queries.size());
        long total = queryRepository.findAll().stream().filter(q -> q.getQuery() instanceof EvaluationQuery && !(q.getQuery() instanceof DiscoveryQuery) && q.getQuery().isPublic()).map(q -> ((EvaluationQuery) q.getQuery())).count() / size;
        response.put("totalPages", (total < 1) ? 1 : total);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Public discovery queries", description = "Return all public discovery queries")
    @RequestMapping(value = "discovery", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> getAllDiscoveries(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "3") int size) {
        List<DiscoveryQuery> queries = new ArrayList<>();
        long skipN = (long) page * size;
        queryRepository.findAll().stream().filter(q -> q.getQuery() instanceof DiscoveryQuery && q.getQuery().isPublic()).map(q -> ((DiscoveryQuery) q.getQuery())).skip(skipN).limit(size).forEachOrdered(queries::add);

        Map<String, Object> response = new HashMap<>();
        response.put("queries", queries);
        response.put("currentPage", page);
        response.put("totalItems", queries.size());
        long total = queryRepository.findAll().stream().filter(q -> q.getQuery() instanceof DiscoveryQuery && q.getQuery().isPublic()).map(q -> ((DiscoveryQuery) q.getQuery())).count() / size;
        response.put("totalPages", (total < 1) ? 1 : total);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
