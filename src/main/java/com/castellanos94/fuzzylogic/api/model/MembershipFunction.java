package com.castellanos94.fuzzylogic.api.model;

import com.castellanos94.fuzzylogic.api.model.impl.membershipfunction.*;
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
        @JsonSubTypes.Type(value = FPG.class, names = {"fpg", "FPG"}),
        @JsonSubTypes.Type(value = Sigmoid.class, names = {"sigmoid", "SIGMOID"}),
        @JsonSubTypes.Type(value = NSigmoid.class, names = {"-sigmoid", "-SIGMOID", "nsigmoid", "NSIGMOID"}),
        @JsonSubTypes.Type(value = Singleton.class, names = {"singleton", "SINGLETON"}),
        @JsonSubTypes.Type(value = Triangular.class, names = {"triangular", "TRIANGULAR"}),
        @JsonSubTypes.Type(value = Trapezoidal.class, names = {"trapezoidal", "TRAPEZOIDAL"}),
        @JsonSubTypes.Type(value = RTrapezoidal.class, names = {"rtrapezoidal", "RTRAPEZOIDAL"}),
        @JsonSubTypes.Type(value = LTrapezoidal.class, names = {"ltrapezoidal", "LTRAPEZOIDAL"}),
        @JsonSubTypes.Type(value = Gamma.class, names = {"gamma", "GAMMA"}),
        @JsonSubTypes.Type(value = LGamma.class, names = {"lgamma", "LGAMMA"}),
        @JsonSubTypes.Type(value = PseudoExp.class, names = {"pseudo-exp", "PSEUDO-EXP"}),
        @JsonSubTypes.Type(value = Gaussian.class, names = {"gaussian", "GAUSSIAN"}),
        @JsonSubTypes.Type(value = ZForm.class, names = {"zform", "ZFORM"}),
        @JsonSubTypes.Type(value = SForm.class, names = {"sform", "SFORM"}),
        @JsonSubTypes.Type(value = Nominal.class, names = {"nominal", "NOMINAL"}),
        @JsonSubTypes.Type(value = GBell.class, names = {"gbell", "GBELL"}),
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
