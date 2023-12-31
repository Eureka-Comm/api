package com.castellanos94.fuzzylogic.api.model.impl;

import com.castellanos94.fuzzylogic.api.model.Base;
import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.core.StateNode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class LinguisticState extends Base {
    @NotNull
    @NotBlank
    protected String cname;
    @Valid
    protected MembershipFunction f;


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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinguisticState)) return false;
        LinguisticState that = (LinguisticState) o;
        return cname.equals(that.cname) && Objects.equals(f, that.f) && label.equals(that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, cname, f);
    }

    @Override
    public String toString() {
        return "LinguisticState{" +
                "label='" + label + '\'' +
                ", cname='" + cname + '\'' +
                ", f=" + f +
                '}';
    }

    @Override
    public StateNode toInternalObject() {
        StateNode stateNode = new StateNode(label, cname);
        if (f != null) {
            stateNode.setMembershipFunction(f.toInternalObject());
        }
        stateNode.setDescription(description);
        return stateNode;
    }
}
