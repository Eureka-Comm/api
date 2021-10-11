package com.castellanos94.fuzzylogic.api.model.impl.membershipfunction;

import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;

import javax.validation.constraints.NotNull;

public class Nominal extends MembershipFunction {
    @NotNull
    protected String key;
    @NotNull
    protected Double value;
    @NotNull
    protected Double notFoundValue;

    public Nominal() {
        super(MembershipFunctionType.NOMINAL);
    }

    @Override
    public com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject() {
        com.castellanos94.fuzzylogicgp.membershipfunction.Nominal nominal = new com.castellanos94.fuzzylogicgp.membershipfunction.Nominal(key, value);
        if (notFoundValue != null)
            nominal.setNotFoundValue(notFoundValue);
        return nominal;

    }

    @Override
    public Boolean isValid() {
        return key != null && value != null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getNotFoundValue() {
        return notFoundValue;
    }

    public void setNotFoundValue(Double notFoundValue) {
        this.notFoundValue = notFoundValue;
    }
}
