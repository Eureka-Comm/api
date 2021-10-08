package com.castellanos94.fuzzylogic.api.model;


import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;

import javax.validation.constraints.NotBlank;

public abstract class Query {

    @NotBlank
    protected String name;

    protected boolean isPublic;

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    @Hidden
    @JsonIgnore
    public abstract NodeTree getPredicateTree() throws Exception;
}
