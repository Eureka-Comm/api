package com.castellanos94.fuzzylogic.api.model;


import com.castellanos94.fuzzylogicgp.logic.LogicBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Logic {
    public static enum LogicType {gmbc, ambc, zadeh, acf, gmbcv}

    public static enum ImplicationType {
        Natural,
        Zadeh,
        Reichenbach,
        KlirYuan,
        Yager
    }

    @NotNull
    protected LogicType type;
    protected Integer exponent;
    protected ImplicationType implicationType;


    public Integer getExponent() {
        return exponent;
    }

    public ImplicationType getImplicationType() {
        return implicationType;
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

    public void setImplicationType(ImplicationType implicationType) {
        this.implicationType = implicationType;
    }

    public com.castellanos94.fuzzylogicgp.logic.LogicBuilder toInternalObject() {
        LogicBuilder lb = LogicBuilder.newBuilder(com.castellanos94.fuzzylogicgp.logic.LogicType.valueOf(type.name().toUpperCase()));
        lb.setExponent(exponent);
        if (implicationType != null){
            lb.setImplicationType(com.castellanos94.fuzzylogicgp.logic.ImplicationType.searchEnum(implicationType.name()));
        }
        //lb.setImplicationType(com.castellanos94.fuzzylogicgp.logic.Logic.ImplicationType.);
        return lb;
    }

    @Override
    public String toString() {
        return "Logic{" +
                "type=" + type +
                ", exponent=" + exponent +
                ", implication type=" + implicationType +
                '}';
    }
}
