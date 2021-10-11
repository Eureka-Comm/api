package com.castellanos94.fuzzylogic.api.model.impl.membershipfunction;
import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
public class FPG extends MembershipFunction {
    @NotNull
    protected Double beta;
    @NotNull
    protected Double gamma;
    @NotNull
    protected Double m;

    public FPG() {
        super(MembershipFunctionType.FPG);
    }

    public Double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public Double getM() {
        return m;
    }

    public void setM(double m) {
        this.m = m;
    }

    @Override
    public com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject() {
        return new com.castellanos94.fuzzylogicgp.membershipfunction.FPG(beta, gamma, m);
    }

    @Override
    public Boolean isValid() {
        return beta != null && gamma != null && m != null;
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
