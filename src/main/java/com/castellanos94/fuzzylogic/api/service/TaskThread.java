package com.castellanos94.fuzzylogic.api.service;

import com.castellanos94.fuzzylogic.api.db.EurekaTask;
import com.castellanos94.fuzzylogic.api.db.EurekaTaskRepository;
import com.castellanos94.fuzzylogic.api.db.FileUtils;
import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import com.castellanos94.fuzzylogicgp.algorithm.EvaluatePredicate;
import com.castellanos94.fuzzylogicgp.algorithm.KDFLC;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Component
@Scope("prototype")
public class TaskThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskThread.class);
    private final EurekaTask task;
    @Autowired
    EurekaTaskRepository repository;

    public TaskThread(EurekaTask task) {
        this.task = task;
    }


    @Override
    public void run() {
        LOGGER.info("Start task:" + task.getId());
        repository.save(task.setStatus(EurekaTask.Status.Running));
        NodeTree predicateTree = null;
        try {
            predicateTree = task.getQuery().getPredicateTree();
        } catch (Exception e) {
            LOGGER.error("Convert predicate", e);
            task.setMsg("Error:" + e.getMessage());
            task.setStatus(EurekaTask.Status.Failed);
        }
        if (predicateTree != null) {
            com.castellanos94.fuzzylogicgp.logic.Logic _logic = null;
            File output;
            Table table = null;
            try {
                table = FileUtils.LOAD_DATASET(task.getId());
            } catch (IOException e) {
                LOGGER.error("Error al leer el dataset", e);
                task.setMsg("Error reading dataset:" + e.getMessage());
                task.setStatus(EurekaTask.Status.Failed);
            }
            if (table != null) {
                if (task.getQuery() instanceof EvaluationQuery && !(task.getQuery() instanceof DiscoveryQuery)) {
                    _logic = ((EvaluationQuery) task.getQuery()).getLogic().toInternalObject().build();
                    System.out.println(((EvaluationQuery)task.getQuery()).getLogic());
                    System.out.println( ((EvaluationQuery) task.getQuery()).getLogic().toInternalObject());
                    EvaluatePredicate evaluatePredicate = new EvaluatePredicate(_logic, table);
                    try {
                        evaluatePredicate.evaluate(predicateTree);
                    } catch (Exception e) {
                        LOGGER.error("Evaluation algorithm", e);
                        task.setMsg("Failed " + new Date());
                        task.setStatus(EurekaTask.Status.Failed);
                    }
                    output = FileUtils.GET_OUTPUT_FILE(task.getId());
                    try {
                        evaluatePredicate.exportToCsv(output.getAbsolutePath());
                        Table result = Table.read().csv(output);
                        StringColumn predicateColumn = StringColumn.create("data");
                        predicateColumn.append(evaluatePredicate.getPredicate().toJson());
                        for (int i = 1; i < result.rowCount(); ++i) {
                            predicateColumn.append("");
                        }
                        result.addColumns(predicateColumn);
                        result.write().csv(output);
                        task.setMsg("Done " + new Date());
                        task.setStatus(EurekaTask.Status.Done);
                    } catch (IOException e) {
                        LOGGER.error("Error al guardar resultados", e);
                        task.setMsg("Error saving dataset:" + e.getMessage());
                        task.setStatus(EurekaTask.Status.Failed);
                    }
                } else if (task.getQuery() instanceof DiscoveryQuery) {
                    DiscoveryQuery discoveryQuery = (DiscoveryQuery) task.getQuery();
                    _logic = discoveryQuery.getLogic().toInternalObject().build();
                    KDFLC algorithm = new KDFLC(_logic, discoveryQuery.getPopulationSize(), discoveryQuery.getNumberOfIterations(),
                            discoveryQuery.getNumberOfResults(), discoveryQuery.getMinimumTruthValue(), discoveryQuery.getMutationRate(),
                            discoveryQuery.getAdjPopulationSize(), discoveryQuery.getAdjNumberOfIterations(), discoveryQuery.getAdjMinimumTruthValue(), table);
                    try {
                        algorithm.execute(predicateTree);
                        output = FileUtils.GET_OUTPUT_FILE(task.getId());
                        algorithm.exportResult(output);
                        task.setMsg("Done " + new Date());
                        task.setStatus(EurekaTask.Status.Done);
                    } catch (Exception e) {
                        LOGGER.error("Discovery algorithm", e);
                        task.setMsg("Failed " + new Date());
                        task.setStatus(EurekaTask.Status.Failed);
                    }
                }
            }
        }
        repository.save(task);
        LOGGER.info("End task:" + task.getId());
    }


}