package com.castellanos94.fuzzylogic.api.model;

import com.castellanos94.fuzzylogicgp.core.Node;

import javax.validation.constraints.NotBlank;

public abstract class Base {
    @NotBlank
    protected String label;
    protected String description;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public abstract Node toInternalObject();
}
