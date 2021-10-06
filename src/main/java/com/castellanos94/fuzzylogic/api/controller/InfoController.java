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
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<List<EvaluationQuery>> getAllPublicEvaluations() {
        List<EvaluationQuery> queries = new ArrayList<>();
        queryRepository.findAll().stream().filter(q -> q.getQuery() instanceof EvaluationQuery && q.getQuery().isPublic()).map(q -> ((EvaluationQuery) q.getQuery())).forEachOrdered(queries::add);
        if (queries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(queries);
    }

    @Operation(summary = "Public discovery queries", description = "Return all public discovery queries")
    @RequestMapping(value = "discovery", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<List<DiscoveryQuery>> getAll() {
        List<DiscoveryQuery> queries = new ArrayList<>();
        queryRepository.findAll().stream().filter(q -> q.getQuery() instanceof DiscoveryQuery && q.getQuery().isPublic()).map(q -> ((DiscoveryQuery) q.getQuery())).forEachOrdered(queries::add);
        if (queries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(queries);
    }
}
