package com.castellanos94.fuzzylogic.api.model.impl.membershipfunction;

import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;

import javax.validation.constraints.NotNull;

public class Gaussian extends MembershipFunction {
    @NotNull
    protected Double center;
    @NotNull
    protected Double deviation;

    public Gaussian() {
        super(MembershipFunctionType.GAUSSIAN);
    }

    @Override
    public com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject() {
        return new com.castellanos94.fuzzylogicgp.membershipfunction.Gaussian(center, deviation);
    }

    @Override
    public Boolean isValid() {
        return center != null && deviation != null;
    }

    public Double getCenter() {
        return center;
    }

    public void setCenter(Double center) {
        this.center = center;
    }

    public Double getDeviation() {
        return deviation;
    }

    public void setDeviation(Double deviation) {
        this.deviation = deviation;
    }
}
