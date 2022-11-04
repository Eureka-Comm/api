package com.castellanos94.fuzzylogic.api.service;

import com.castellanos94.fuzzylogic.api.db.EurekaTask;
import com.castellanos94.fuzzylogic.api.db.EurekaTaskRepository;
import com.castellanos94.fuzzylogic.api.db.ResultWrapper;
import com.castellanos94.fuzzylogic.api.db.ResultWrapperRepository;
import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import com.castellanos94.fuzzylogic.api.utils.FileUtils;
import com.castellanos94.fuzzylogic.api.utils.TransformPredicate;
import com.castellanos94.fuzzylogicgp.core.DiscoveryResult;
import com.castellanos94.fuzzylogicgp.core.EvaluationResult;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.OperatorException;
import com.castellanos94.jfuzzylogic.algorithm.impl.DiscoveryAlgorithm;
import com.castellanos94.jfuzzylogic.algorithm.impl.EvaluationAlgorithm;
import com.castellanos94.jfuzzylogic.core.OperatorUtil;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.logic.ImplicationType;
import com.castellanos94.jfuzzylogic.core.logic.LogicType;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import tech.tablesaw.api.Table;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

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


    @Override
    public void run() {
        LOGGER.info("Start task:" + task.getId());
        repository.save(task.setStatus(EurekaTask.Status.Running).setStart(new Date()));
        NodeTree predicateTree = null;
        try {
            predicateTree = task.getQuery().getPredicateTree();
        } catch (Exception e) {
            LOGGER.error("Convert predicate", e);
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            task.setMsg("Error:" + writer.toString());
            task.setStatus(EurekaTask.Status.Failed);
        }
        if (predicateTree != null) {
            com.castellanos94.jfuzzylogic.core.logic.Logic _logic;
            File output;
            Table table = null;
            try {
                table = FileUtils.LOAD_DATASET(task.getId());
            } catch (IOException e) {
                LOGGER.error("Error al leer el dataset", e);
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                task.setMsg("Error reading dataset:" + writer.toString());
                task.setStatus(EurekaTask.Status.Failed);
            }
            if (table != null) {
                ResultWrapper results = new ResultWrapper();
                results.setTaskId(task.getId());
                results.setJob(task.getQuery().getJob());
                Operator _operator = null;
                try {
                    _operator = TransformPredicate.transform(predicateTree);
                } catch (JsonProcessingException e) {
                    LOGGER.error("Error al transformar el predicado", e);
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));
                    task.setMsg("Error convert predicate:" + writer.toString());
                    task.setStatus(EurekaTask.Status.Failed);
                }
                _logic = com.castellanos94.jfuzzylogic.core.logic.impl.LogicBuilder
                        .newBuilder(LogicType.valueOf(task.getQuery().getLogic().getType().name().toUpperCase()))
                        .setExponent(task.getQuery().getLogic().getExponent())
                        .setImplicationType(ImplicationType.searchEnum(task.getQuery().getLogic().getImplicationType().name()))
                        .create();
                LOGGER.info("Original predicate {}", predicateTree);
                LOGGER.info("Predicate to work {}", _operator);
                if (_logic == null) {
                    LOGGER.error("Error al transformar la logica");
                    task.setMsg("Error convert predicate: logic");
                    task.setStatus(EurekaTask.Status.Failed);
                } else {
                    if (task.getQuery() instanceof EvaluationQuery && !(task.getQuery() instanceof DiscoveryQuery)) {
                        LOGGER.error("Logic {}", _logic);

                        try {
                            EvaluationAlgorithm evaluatePredicate = new EvaluationAlgorithm(_operator, _logic, table);
                            evaluatePredicate.execute();
                            ArrayList<NodeTree> operators = NodeTree.getNodesByType(predicateTree, NodeTree.class);
                            com.castellanos94.jfuzzylogic.core.base.impl.EvaluationResult rs = evaluatePredicate.getResult();

                            EvaluationResult evaluationResult = new EvaluationResult(rs.getForAll(), rs.getExists(), rs.getData().remove("result"), Collections.emptyMap());

                            for (Operator op : OperatorUtil.getNodesByClass(_operator, Operator.class)) {
                                EvaluationAlgorithm _evaluatePredicate = new EvaluationAlgorithm(op, _logic, table);

                                _evaluatePredicate.execute();

                            }
                            evaluationResult.setPredicate(TransformPredicate.convertToOldVersion(rs.getPredicate()).toJson());


                            results.setResult(evaluationResult);
                            resultRepository.save(results);

                            task.setMsg("Done " + new Date());
                            task.setStatus(EurekaTask.Status.Done);
                        } catch (Exception e) {
                            LOGGER.error("Evaluation algorithm (or saving dataset)", e);
                            StringWriter writer = new StringWriter();
                            e.printStackTrace(new PrintWriter(writer));
                            task.setMsg("Failed " + new Date() + " " + writer.toString());
                            task.setStatus(EurekaTask.Status.Failed);
                        }
                    } else if (task.getQuery() instanceof DiscoveryQuery) {
                        try {
                            DiscoveryQuery discoveryQuery = (DiscoveryQuery) task.getQuery();
                            LOGGER.error("Logic {}", _logic);
                            DiscoveryAlgorithm discoveryAlgorithm = new DiscoveryAlgorithm(_operator, discoveryQuery.getMaxTime(), _logic, table,
                                    (double) discoveryQuery.getMinimumTruthValue(), 0.95, (double) discoveryQuery.getMutationRate(),
                                    discoveryQuery.getNumberOfResults(), discoveryQuery.getPopulationSize(), (double) discoveryQuery.getAdjMinimumTruthValue(), 0.95,
                                    0.1, discoveryQuery.getAdjPopulationSize(), discoveryQuery.getAdjNumberOfIterations());
                            discoveryAlgorithm.execute();
                            List<DiscoveryResult.Record> values = new ArrayList<>();

                            boolean flag = _operator instanceof Imp;
                            Table finalTable = table;
                            discoveryAlgorithm.getResult().getData().forEach(row -> {
                                if (flag) {
                                    double before = row.getFitness();
                                    EvaluationAlgorithm evaluationAlgorithm = new EvaluationAlgorithm(row, _logic, finalTable);
                                    LOGGER.error("Evaluation for discovery implication {} - {}", before, row.getFitness());
                                }
                                NodeTree tree = null;
                                try {
                                    tree = TransformPredicate.convertToOldVersion(row);
                                    values.add(new DiscoveryResult.Record(tree.getFitness(), tree.toString(), tree.toJson()));
                                } catch (OperatorException e) {
                                    LOGGER.error("Discovery algorithm export convert", e);
                                    StringWriter writer = new StringWriter();
                                    e.printStackTrace(new PrintWriter(writer));
                                    task.setMsg("Failed " + new Date() + " " + writer.toString());
                                    task.setStatus(EurekaTask.Status.Failed);
                                }
                            });
                            if (flag) {
                                values.sort(Comparator.comparingDouble(DiscoveryResult.Record::getFitness)
                                        .reversed());
                            }
                            DiscoveryResult discoveryResult = new DiscoveryResult(values);
                            results.setResult(discoveryResult);
                            resultRepository.save(results);

                            task.setMsg("Done " + new Date());
                            task.setStatus(EurekaTask.Status.Done);
                        } catch (Exception e) {
                            LOGGER.error("Discovery algorithm", e);
                            StringWriter writer = new StringWriter();
                            e.printStackTrace(new PrintWriter(writer));
                            task.setMsg("Failed " + new Date() + " " + writer.toString());
                            task.setStatus(EurekaTask.Status.Failed);
                        }
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
}

