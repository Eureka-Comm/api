package com.castellanos94.fuzzylogic.api.model.impl;

import com.castellanos94.fuzzylogic.api.model.Base;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class Generator extends Base {
    public enum Operators {AND, OR, NOT, IMP, EQV}

    @NotNull
    @NotEmpty
    protected Set<Operators> operators;
    @NotNull
    @NotEmpty
    protected Set<String> variables;
    @NotNull
    protected Integer depth;

    public Set<Operators> getOperators() {
        return operators;
    }

    public void setOperators(Set<Operators> operators) {
        this.operators = operators;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public void setVariables(Set<String> variables) {
        this.variables = variables;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "Generator{" +
                "label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", operators=" + operators +
                ", variables=" + variables +
                ", depth=" + depth +
                '}';
    }
}
