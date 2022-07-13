package com.castellanos94.fuzzylogic.api.service;

import com.castellanos94.fuzzylogic.api.db.*;
import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import com.castellanos94.fuzzylogic.api.utils.FileUtils;
import com.castellanos94.fuzzylogicgp.algorithm.EvaluatePredicate;
import com.castellanos94.fuzzylogicgp.algorithm.KDFLC;
import com.castellanos94.fuzzylogicgp.core.DiscoveryResult;
import com.castellanos94.fuzzylogicgp.core.EvaluationResult;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Component
@Scope("prototype")
public class TaskThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskThread.class);
    private final EurekaTask task;
    @Autowired
    EurekaTaskRepository repository;

    @Autowired
    ResultWrapperRepository resultRepository;

    public TaskThread(EurekaTask task) {
        this.task = task;
    }

    private KDFLC algorithm;

    @Override
    public void run() {
        LOGGER.info("Start task:" + task.getId());
        repository.save(task.setStatus(EurekaTask.Status.Running).setStart(new Date()));
        NodeTree predicateTree = null;
        try {
            predicateTree = task.getQuery().getPredicateTree();
        } catch (Exception e) {
            LOGGER.error("Convert predicate", e);
            task.setMsg("Error:" + e.getMessage());
            task.setStatus(EurekaTask.Status.Failed);
        }
        if (predicateTree != null) {
            com.castellanos94.fuzzylogicgp.logic.Logic _logic ;
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
                ResultWrapper results = new ResultWrapper();
                results.setTaskId(task.getId());
                results.setJob(task.getQuery().getJob());

                if (task.getQuery() instanceof EvaluationQuery && !(task.getQuery() instanceof DiscoveryQuery)) {
                    _logic = task.getQuery().getLogic().toInternalObject().build();
                    LOGGER.error("Logic {}", _logic);
                    try {
                        EvaluatePredicate evaluatePredicate = new EvaluatePredicate(_logic, table);
                        evaluatePredicate.evaluate(predicateTree);
                        ArrayList<NodeTree> operators = NodeTree.getNodesByType(predicateTree, NodeTree.class);
                        EvaluationResult evaluationResult = (EvaluationResult) evaluatePredicate.getResult();
                        for (NodeTree op : operators) {
                            EvaluatePredicate evaluatePredicate2 = new EvaluatePredicate(_logic, table);
                            op.setFitness(evaluatePredicate2.evaluate(op.copy()));
                        }
                        evaluationResult.setPredicate(predicateTree.toJson());

                        results.setResult(evaluationResult);
                        resultRepository.save(results);

                        task.setMsg("Done " + new Date());
                        task.setStatus(EurekaTask.Status.Done);
                    } catch (Exception e) {
                        LOGGER.error("Evaluation algorithm (or saving dataset)", e);
                        task.setMsg("Failed " + new Date() + " " + e.getMessage());
                        task.setStatus(EurekaTask.Status.Failed);
                    }
                } else if (task.getQuery() instanceof DiscoveryQuery) {
                    try {
                        DiscoveryQuery discoveryQuery = (DiscoveryQuery) task.getQuery();
                        _logic = discoveryQuery.getLogic().toInternalObject().build();
                        LOGGER.error("Logic {}", _logic);
                        algorithm = new KDFLC(_logic, discoveryQuery.getPopulationSize(), discoveryQuery.getNumberOfIterations(),
                                discoveryQuery.getNumberOfResults(), discoveryQuery.getMinimumTruthValue(), discoveryQuery.getMutationRate(),
                                discoveryQuery.getAdjPopulationSize(), discoveryQuery.getAdjNumberOfIterations(), discoveryQuery.getAdjMinimumTruthValue(), table, discoveryQuery.getMaxTime());

                        algorithm.execute(predicateTree);
                        DiscoveryResult discoveryResult = (DiscoveryResult) algorithm.getResult();

                        results.setResult(discoveryResult);
                        resultRepository.save(results);

                        task.setMsg("Done " + new Date());
                        task.setStatus(EurekaTask.Status.Done);
                    } catch (Exception e) {
                        LOGGER.error("Discovery algorithm", e);
                        task.setMsg("Failed " + new Date() + " " + e.getMessage());
                        task.setStatus(EurekaTask.Status.Failed);
                    }
                }
            }
        } else {
            LOGGER.error("Invalid input " + task.getId());
            task.setMsg("Invalid predicate " + new Date());
            task.setStatus(EurekaTask.Status.Failed);
        }
        repository.save(task.setEnd(new Date()));
        if (!task.getQuery().isPublic()) {
            FileUtils.DELETE_DATASET(task.getId());
        }
        LOGGER.info("End task:" + task.getId());
    }

    public EurekaTask getTask() {
        return task;
    }

    public void stop() {
        // TODO: invocar a detener algoritmo y guardar en base de datos
        // Posible escenario: que se detenga sin guardar estado Status.Stopped Status.Running
        // Guardar estado Status.Pause ? -> Status.Running
        // Solo guardar los resultados y exportar Status.Done -> Terminal
    }

    public ArrayList<String> getLog() {
        if (algorithm != null)
            return algorithm.getLogList();
        return null;
    }
}

