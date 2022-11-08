package com.castellanos94.fuzzylogic.api.model.impl.membershipfunction;

import com.castellanos94.fuzzylogic.api.model.MembershipFunction;
import com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunctionType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

public class MapNominal extends MembershipFunction {
    @NotNull
    protected HashMap<String, Double> values;
    @NotNull
    @Min(value = 0)
    @Max(value = 1)
    protected Double notFoundValue;

    public MapNominal() {
        super(MembershipFunctionType.MAPNOMIAL);
    }

    @Override
    public com.castellanos94.fuzzylogicgp.membershipfunction.MembershipFunction toInternalObject() {
        com.castellanos94.fuzzylogicgp.membershipfunction.MapNominal m = new com.castellanos94.fuzzylogicgp.membershipfunction.MapNominal();
        m.setEditable(false);
        m.setNotFoundValue(this.notFoundValue);
        m.setValues(values);
        return m;
    }

    @Override
    public Boolean isValid() {
        return values != null && notFoundValue != null;
    }
}
