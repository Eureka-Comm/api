package com.castellanos94.fuzzylogic.api.model.impl.membershipfunction;

import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;
import io.swagger.v3.oas.annotations.Hidden;

import javax.validation.constraints.NotNull;


public class Sigmoid extends MembershipFunction {
    @NotNull
    private Double center;
    @NotNull
    private Double beta;

    public Sigmoid() {
        super(MembershipFunctionType.SIGMOID);
    }


    @Override
    public com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject() {
        return new com.castellanos94.fuzzylogicgp.membershipfunction.Sigmoid(center, beta);
    }

    public Double getCenter() {
        return center;
    }

    public void setCenter(Double center) {
        this.center = center;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    @Override
    @Hidden
    public Boolean isValid() {
        return center != null && beta != null;
    }

    @Override
    public String toString() {
        return "Sigmoid{" +
                "center=" + center +
                ", beta=" + beta +
                '}';
    }
}
