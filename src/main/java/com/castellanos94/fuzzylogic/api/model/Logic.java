package com.castellanos94.fuzzylogic.api.model;


import com.castellanos94.fuzzylogicgp.logic.LogicBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Logic {
    public static enum LogicType {gmbc, ambc, zadeh, acf, gmbcv}

    @NotNull
    protected LogicType type;
    protected Integer exponent;
    protected Boolean natural_implication;

    public Boolean getNatural_implication() {
        return natural_implication;
    }

    public Integer getExponent() {
        return exponent;
    }

    public LogicType getType() {
        return type;
    }

    public void setType(LogicType type) {
        this.type = type;
    }

    public void setExponent(Integer exponent) {
        this.exponent = exponent;
    }

    public void setNatural_implication(Boolean natural_implication) {
        this.natural_implication = natural_implication;
    }

    public com.castellanos94.fuzzylogicgp.logic.LogicBuilder toInternalObject() {
        LogicBuilder lb = LogicBuilder.newBuilder(com.castellanos94.fuzzylogicgp.logic.LogicType.valueOf(type.name().toUpperCase()));
        lb.setExponent(exponent);
        if (natural_implication != null)
            lb.setNatural_implication(natural_implication);
        return lb;
    }

    @Override
    public String toString() {
        return "Logic{" +
                "type=" + type +
                ", exponent=" + exponent +
                ", natural_implication=" + natural_implication +
                '}';
    }
}
