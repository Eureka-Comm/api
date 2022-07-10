package com.castellanos94.fuzzylogic.api.model;


import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.TaskType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.Hidden;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property= "job")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EvaluationQuery.class, names = {"EVALUATION","evaluation"}),
        @JsonSubTypes.Type(value = DiscoveryQuery.class, names = {"DISCOVERY","discovery"})}
)
public abstract class Query {
    @NotNull
    public TaskType job;
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
