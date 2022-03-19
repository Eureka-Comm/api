package com.castellanos94.fuzzylogic.api.service;

import com.castellanos94.fuzzylogic.api.db.EurekaTask;
import com.castellanos94.fuzzylogic.api.db.EurekaTaskRepository;
import com.castellanos94.fuzzylogic.api.db.FileUtils;
import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import com.castellanos94.fuzzylogicgp.algorithm.EvaluatePredicate;
import com.castellanos94.fuzzylogicgp.algorithm.KDFLC;
import com.castellanos94.fuzzylogicgp.core.Node;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.StringColumn;
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
                    LOGGER.error("Logic {}", _logic);
                    try {
                        EvaluatePredicate evaluatePredicate = new EvaluatePredicate(_logic, table);
                        evaluatePredicate.evaluate(predicateTree);
                        ArrayList<Node> operators = NodeTree.getNodesByType(predicateTree, NodeType.AND);
                        for (Node node : operators) {
                            if (node instanceof NodeTree && !node.equals(predicateTree)) {
                                EvaluatePredicate ep = new EvaluatePredicate(_logic, table);
                                double evaluate = ep.evaluate((NodeTree) node);
                                ((NodeTree) node).setFitness(evaluate);
                            }
                        }
                        operators = NodeTree.getNodesByType(predicateTree, NodeType.OR);
                        for (Node node : operators) {
                            if (node instanceof NodeTree && !node.equals(predicateTree)) {
                                EvaluatePredicate ep = new EvaluatePredicate(_logic, table);
                                double evaluate = ep.evaluate((NodeTree) node);
                                ((NodeTree) node).setFitness(evaluate);
                            }
                        }
                        operators = NodeTree.getNodesByType(predicateTree, NodeType.NOT);
                        for (Node node : operators) {
                            if (node instanceof NodeTree && !node.equals(predicateTree)) {
                                EvaluatePredicate ep = new EvaluatePredicate(_logic, table);
                                double evaluate = ep.evaluate((NodeTree) node);
                                ((NodeTree) node).setFitness(evaluate);
                            }
                        }
                        operators = NodeTree.getNodesByType(predicateTree, NodeType.IMP);
                        for (Node node : operators) {
                            if (node instanceof NodeTree && !node.equals(predicateTree)) {
                                EvaluatePredicate ep = new EvaluatePredicate(_logic, table);
                                double evaluate = ep.evaluate((NodeTree) node);
                                ((NodeTree) node).setFitness(evaluate);
                            }
                        }
                        operators = NodeTree.getNodesByType(predicateTree, NodeType.EQV);
                        for (Node node : operators) {
                            if (node instanceof NodeTree && !node.equals(predicateTree)) {
                                EvaluatePredicate ep = new EvaluatePredicate(_logic, table);
                                double evaluate = ep.evaluate((NodeTree) node);
                                ((NodeTree) node).setFitness(evaluate);
                            }
                        }
                        output = FileUtils.GET_OUTPUT_FILE(task.getId());

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
                        KDFLC algorithm = new KDFLC(_logic, discoveryQuery.getPopulationSize(), discoveryQuery.getNumberOfIterations(),
                                discoveryQuery.getNumberOfResults(), discoveryQuery.getMinimumTruthValue(), discoveryQuery.getMutationRate(),
                                discoveryQuery.getAdjPopulationSize(), discoveryQuery.getAdjNumberOfIterations(), discoveryQuery.getAdjMinimumTruthValue(), table);

                        algorithm.execute(predicateTree);
                        output = FileUtils.GET_OUTPUT_FILE(task.getId());
                        try {
                            algorithm.exportResult(output);
                            task.setMsg("Done " + new Date());
                            task.setStatus(EurekaTask.Status.Done);
                        } catch (Exception e) {
                            LOGGER.error("Error trying export", e);
                            task.setMsg("Error during export of results" + new Date() + " " + e.getMessage());
                            task.setStatus(EurekaTask.Status.Failed);
                        }
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
        repository.save(task);
        if (!task.getQuery().isPublic()) {
            FileUtils.DELETE_DATASET(task.getId());
        }
        LOGGER.info("End task:" + task.getId());
    }

}