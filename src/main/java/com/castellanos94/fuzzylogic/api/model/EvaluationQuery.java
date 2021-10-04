package com.castellanos94.fuzzylogic.api.model;

import com.castellanos94.fuzzylogic.api.model.impl.LinguisticState;
import com.castellanos94.fuzzylogicgp.logic.LogicType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class EvaluationQuery {
    @NotBlank
    protected String name;
    protected String description;
    @NotBlank
    protected String dataset;
    @NotEmpty
    protected Set<LinguisticState> states;
    @NotNull
    protected LogicType logicType;
    @NotBlank
    protected String predicate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public Set<LinguisticState> getStates() {
        return states;
    }

    public void setStates(Set<LinguisticState> states) {
        this.states = states;
    }

    public LogicType getLogicType() {
        return logicType;
    }

    public void setLogicType(LogicType logicType) {
        this.logicType = logicType;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    @Override
    public String toString() {
        return "EvaluationQuery{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dataset='" + dataset + '\'' +
                ", states=" + states +
                ", logicType=" + logicType +
                ", predicate='" + predicate + '\'' +
                '}';
    }
}
