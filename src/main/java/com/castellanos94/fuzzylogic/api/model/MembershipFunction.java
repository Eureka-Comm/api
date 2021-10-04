package com.castellanos94.fuzzylogic.api.model;

import com.castellanos94.fuzzylogic.api.model.impl.*;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.validation.constraints.NotNull;
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Sigmoid.class, name="sigmoid"),
        @JsonSubTypes.Type(value = Triangular.class,name="triangular"),
        @JsonSubTypes.Type(value = FPG.class,name = "fpg")

})
public abstract class MembershipFunction {
    @NotNull
    protected final MembershipFunctionType type;

    protected MembershipFunction(MembershipFunctionType type) {
        this.type = type;
    }

    public MembershipFunctionType getType() {
        return type;
    }

    public abstract com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject();
}
