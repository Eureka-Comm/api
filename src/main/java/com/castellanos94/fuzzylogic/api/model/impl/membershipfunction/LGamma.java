package com.castellanos94.fuzzylogic.api.model.impl.membershipfunction;

import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;

import javax.validation.constraints.NotNull;

public class LGamma extends MembershipFunction {
    @NotNull
    protected Double a;
    @NotNull
    protected Double b;

    public LGamma() {
        super(MembershipFunctionType.LGAMMA);
    }

    @Override
    public com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject() {
        return new com.castellanos94.fuzzylogicgp.membershipfunction.LGamma(a, b);
    }

    @Override
    public Boolean isValid() {
        return a != null && b != null;
    }

    public Double getA() {
        return a;
    }

    public void setA(Double a) {
        this.a = a;
    }

    public Double getB() {
        return b;
    }

    public void setB(Double b) {
        this.b = b;
    }
}
