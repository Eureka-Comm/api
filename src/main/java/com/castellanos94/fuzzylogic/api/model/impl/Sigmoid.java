package com.castellanos94.fuzzylogic.api.model.impl;

import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;

import javax.validation.constraints.NotNull;

public class Sigmoid extends MembershipFunction {
    @NotNull
    private double center;
    @NotNull
    private double beta;
    protected Sigmoid() {
        super(MembershipFunctionType.SIGMOID);
    }

    public double getCenter() {
        return center;
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    @Override
    public com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject() {
        return new com.castellanos94.fuzzylogicgp.membershipfunction.Sigmoid(center,beta);
    }

    @Override
    public String toString() {
        return "Sigmoid{" +
                "center=" + center +
                ", beta=" + beta +
                '}';
    }
}
