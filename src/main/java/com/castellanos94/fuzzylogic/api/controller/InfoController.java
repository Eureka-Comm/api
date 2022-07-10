package com.castellanos94.fuzzylogic.api.controller;

import com.castellanos94.fuzzylogic.api.db.EurekaTask;
import com.castellanos94.fuzzylogic.api.db.EurekaTaskRepository;
import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import com.castellanos94.fuzzylogic.api.service.AsynchronousService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
        Pageable paging = PageRequest.of(page, size);

        Page<EurekaTask> queryPage = queryRepository.findPublicByTaskType(EvaluationQuery.class.getName(), paging);

        Map<String, Object> response = new HashMap<>();
        response.put("queries", queryPage.getContent());
        response.put("currentPage", queryPage.getNumber());
        response.put("totalItems", queryPage.getTotalElements());
        response.put("totalPages", queryPage.getTotalPages());


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Public discovery queries", description = "Return all public discovery queries")
    @RequestMapping(value = "discovery", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> getAllDiscoveries(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "3") int size) {
        Pageable paging = PageRequest.of(page, size);

        Page<EurekaTask> queryPage = queryRepository.findPublicByTaskType(DiscoveryQuery.class.getName(), paging);

        Map<String, Object> response = new HashMap<>();
        response.put("queries", queryPage.getContent());
        response.put("currentPage", queryPage.getNumber());
        response.put("totalItems", queryPage.getTotalElements());
        response.put("totalPages", queryPage.getTotalPages());


        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
