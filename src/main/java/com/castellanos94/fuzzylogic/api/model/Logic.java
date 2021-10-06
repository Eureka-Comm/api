package com.castellanos94.fuzzylogic.api.model;


import javax.validation.constraints.NotNull;

public class Logic {
    public static enum LogicType{gmbc,ambc,zadeh,acf,gmbcv}
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
}
