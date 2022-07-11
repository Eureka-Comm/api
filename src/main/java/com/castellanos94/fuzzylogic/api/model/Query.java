package com.castellanos94.fuzzylogic.api.model;


import com.castellanos94.fuzzylogic.api.model.impl.DiscoveryQuery;
import com.castellanos94.fuzzylogic.api.model.impl.EvaluationQuery;
import com.castellanos94.fuzzylogic.api.model.impl.LinguisticState;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.core.TaskType;
import com.castellanos94.fuzzylogicgp.parser.ParserPredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.Hidden;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "job")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EvaluationQuery.class, names = {"EVALUATION", "evaluation"}),
        @JsonSubTypes.Type(value = DiscoveryQuery.class, names = {"DISCOVERY", "discovery"})}
)
public abstract class Query {
    @NotNull
    public TaskType job;
    @NotBlank
    protected String name;

    protected Set<String> tags;

    protected boolean isPublic;

    protected String description;

    @NotEmpty
    protected Set<LinguisticState> states;
    @NotNull
    protected Logic logic;
    @NotBlank
    protected String predicate;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Set<LinguisticState> getStates() {
        return states;
    }

    public void setStates(Set<LinguisticState> states) {
        this.states = states;
    }

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    @Hidden
    @JsonIgnore
    protected ArrayList<StateNode> convertStates() {
        ArrayList<StateNode> stateNodeArrayList = new ArrayList<>();
        states.stream().map(LinguisticState::toInternalObject).forEach(stateNodeArrayList::add);
        return stateNodeArrayList;
    }
}
