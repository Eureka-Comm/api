package com.castellanos94.fuzzylogic.api.model.impl;

import com.castellanos94.fuzzylogic.api.model.Base;
import com.castellanos94.fuzzylogicgp.core.DummyGenerator;
import com.castellanos94.fuzzylogicgp.core.GeneratorNode;
import com.castellanos94.fuzzylogicgp.core.Node;
import com.castellanos94.fuzzylogicgp.core.NodeType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    /**
     * Dummy Generator
     *
     * @return
     */
    @Override
    public DummyGenerator toInternalObject() {
        DummyGenerator generator = new DummyGenerator();
        generator.setDepth(depth);
        generator.setLabel(label);
        generator.setDescription(description);
        NodeType[] types = new NodeType[operators.size()];
        AtomicInteger integer = new AtomicInteger(0);
        operators.stream().map(o -> NodeType.valueOf(o.name())).forEach(nt -> {
            types[integer.getAndIncrement()] = nt;
        });
        generator.setOperators(types);
        generator.setVariables(new ArrayList<>(variables));
        return generator;
    }

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
