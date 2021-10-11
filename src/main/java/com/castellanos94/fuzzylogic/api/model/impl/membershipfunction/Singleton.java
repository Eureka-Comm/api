package com.castellanos94.fuzzylogic.api.model.impl.membershipfunction;

import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;

import javax.validation.constraints.NotNull;

public class Singleton extends MembershipFunction {
    @NotNull
    protected Double a;

    public Singleton() {
        super(MembershipFunctionType.SINGLETON);
    }

    @Override
    public com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject() {
        return new com.castellanos94.fuzzylogicgp.membershipfunction.Singleton(a);
    }

    @Override
    public Boolean isValid() {
        return a != null;
    }

    public Double getA() {
        return a;
    }

    public void setA(Double a) {
        this.a = a;
    }
}
