package com.castellanos94.fuzzylogic.api.model.impl;

import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;

import javax.validation.constraints.NotNull;
public class FPG extends MembershipFunction {
    @NotNull
    protected double beta;
    @NotNull
    protected double gamma;
    @NotNull
    protected double m;
    public FPG(){
        super(MembershipFunctionType.FPG);
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getM() {
        return m;
    }

    public void setM(double m) {
        this.m = m;
    }

    @Override
    public com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject() {
        return  new com.castellanos94.fuzzylogicgp.membershipfunction.FPG(beta,gamma,m);
    }

    @Override
    public String toString() {
        return "FPG{" +
                "beta=" + beta +
                ", gamma=" + gamma +
                ", m=" + m +
                '}';
    }
}
