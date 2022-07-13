package com.castellanos94.fuzzylogic.api.utils;

import com.castellanos94.fuzzylogicgp.core.DiscoveryResult;
import com.castellanos94.fuzzylogicgp.core.EvaluationResult;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.ResultTask;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction;
import com.castellanos94.fuzzylogicgp.parser.MembershipFunctionSerializer;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultTaskUtils {
    public static void export(File file, ResultTask resultTask) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File path is required");
        }
        Table table;
        if (resultTask instanceof EvaluationResult)
            table = evaluationResultToFile((EvaluationResult) resultTask);
        else
            table = discoveryResultToFile((DiscoveryResult) resultTask);
        table.write().toFile(file);
    }

    private static Table discoveryResultToFile(DiscoveryResult resultTask) {
        Table table = Table.create();


        ArrayList<Double> v = new ArrayList<>();
        ArrayList<String> p = new ArrayList<>();
        ArrayList<String> d = new ArrayList<>();
        List<DiscoveryResult.Record> records = resultTask.getValues();
        for (DiscoveryResult.Record record : records) {
            v.add(record.getFitness());
            p.add(record.getExpression());
            d.add(record.getData());
        }
        DoubleColumn value = DoubleColumn.create("truth-value", v);
        StringColumn predicates = StringColumn.create("predicate", p);
        StringColumn data = StringColumn.create("data", d);
        table.addColumns(value, predicates, data);
        return table;
    }

    private static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(MembershipFunction.class, new MembershipFunctionSerializer());
        // builder.excludeFieldsWithoutExposeAnnotation();
        builder.setExclusionStrategies(new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getName().equalsIgnoreCase("editable");
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }

        });
        return  builder.create();
    }

    private static Table evaluationResultToFile(EvaluationResult resultTask) {
        Table table = Table.create();
        StringColumn fa = StringColumn.create("For All");
        fa.append("" + resultTask.getForAll());
        StringColumn ex = StringColumn.create("Exist");
        ex.append("" + resultTask.getExists());
        DoubleColumn rs = DoubleColumn.create("Result", resultTask.getResult());
        StringColumn data = StringColumn.create("data");
        data.append(resultTask.getPredicate());
        for (int i = 1; i < rs.size(); i++) {
            fa.append("");
            ex.append("");
            data.append("");
        }
        if (resultTask.getExtend() != null && !resultTask.getExtend().isEmpty()) {
            resultTask.getExtend().forEach((k, v) -> {
                table.addColumns(DoubleColumn.create(k, v));
            });
        }
        table.addColumns(fa, ex, rs,data);
        return table;
    }
}
