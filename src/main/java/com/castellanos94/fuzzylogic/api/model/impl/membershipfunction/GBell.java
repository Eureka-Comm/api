package com.castellanos94.fuzzylogic.api.model.impl.membershipfunction;

import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;

import javax.validation.constraints.NotNull;

public class GBell extends MembershipFunction {
    @NotNull
    protected Double width;
    @NotNull
    protected Double slope;
    @NotNull
    protected Double center;

    public GBell() {
        super(MembershipFunctionType.GBELL);
    }

    @Override
    public com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject() {
        return new com.castellanos94.fuzzylogicgp.membershipfunction.GBell(width, slope, center);
    }

    @Override
    public Boolean isValid() {
        return width != null && slope != null && center != null;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getSlope() {
        return slope;
    }

    public void setSlope(Double slope) {
        this.slope = slope;
    }

    public Double getCenter() {
        return center;
    }

    public void setCenter(Double center) {
        this.center = center;
    }
}
