package com.castellanos94.fuzzylogic.api.model.impl;

import com.castellanos94.fuzzylogic.api.model.Logic;
import com.castellanos94.fuzzylogic.api.model.Query;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Document(collection = "queries")
public class EvaluationQuery extends Query {

    protected String description;

    @NotEmpty
    protected Set<LinguisticState> states;
    @NotNull
    protected Logic logic;
    @NotBlank
    protected String predicate;


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

    @Override
    public String toString() {
        return "EvaluationQuery{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", states=" + states +
                ", logic=" + logic +
                ", predicate='" + predicate + '\'' +
                '}';
    }
}
