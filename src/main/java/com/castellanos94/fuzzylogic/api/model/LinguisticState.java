package com.castellanos94.fuzzylogic.api.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public class LinguisticState {
    @NotBlank
    protected String label;
    @NotBlank
    protected String cname;
    protected MembershipFunction f;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public MembershipFunction getF() {
        return f;
    }

    public void setF(MembershipFunction f) {
        this.f = f;
    }

    @Override
    public String toString() {
        return "LinguisticState{" +
                "label='" + label + '\'' +
                ", cname='" + cname + '\'' +
                ", f=" + f +
                '}';
    }
}
