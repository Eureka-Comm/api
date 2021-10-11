package com.castellanos94.fuzzylogic.api.model.impl.membershipfunction;

import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;

import javax.validation.constraints.NotNull;

public class Trapezoidal extends MembershipFunction {
    @NotNull
    protected Double a;
    @NotNull
    protected Double b;
    @NotNull
    protected Double c;
    @NotNull
    protected Double d;

    public Trapezoidal() {
        super(MembershipFunctionType.TRAPEZOIDAL);
    }

    @Override
    public com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject() {
        return new com.castellanos94.fuzzylogicgp.membershipfunction.Trapezoidal(a, b, c, d);
    }

    @Override
    public Boolean isValid() {
        return a != null && b != null && c != null && d != null;
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

    public Double getC() {
        return c;
    }

    public void setC(Double c) {
        this.c = c;
    }

    public Double getD() {
        return d;
    }

    public void setD(Double d) {
        this.d = d;
    }
}
