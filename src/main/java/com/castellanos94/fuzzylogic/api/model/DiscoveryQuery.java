package com.castellanos94.fuzzylogic.api.model;

import com.castellanos94.fuzzylogic.api.model.impl.Generator;

import java.util.ArrayList;

public class DiscoveryQuery extends EvaluationQuery{

    private ArrayList<Generator> generators;

    public ArrayList<Generator> getGenerators() {
        return generators;
    }

    @Override
    public String toString() {
        return "DiscoveryQuery{" +
                "generators=" + generators +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dataset='" + dataset + '\'' +
                ", states=" + states +
                ", logicType=" + logicType +
                ", predicate='" + predicate + '\'' +
                '}';
    }
}
