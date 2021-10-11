package com.castellanos94.fuzzylogic.api.model;

import com.castellanos94.fuzzylogic.api.model.impl.*;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.Hidden;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Sigmoid.class, names = {"sigmoid", "SIGMOID"}),
        @JsonSubTypes.Type(value = Triangular.class, name = "triangular"),
        @JsonSubTypes.Type(value = FPG.class, name = "fpg")

})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public abstract class MembershipFunction {
    @NotNull
    protected MembershipFunctionType type;

    protected MembershipFunction(MembershipFunctionType type) {
        this.type = type;
    }

    public void setType(MembershipFunctionType type) {
        this.type = type;
    }

    public abstract com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject();

    @Hidden
    public abstract Boolean isValid();
}
