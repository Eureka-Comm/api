package com.castellanos94.fuzzylogic.api.model;


import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public abstract class Query {

    @NotBlank
    protected String name;

    protected Set<String> tags;

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

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @Hidden
    @JsonIgnore
    public abstract NodeTree getPredicateTree() throws Exception;
}
