package com.castellanos94.fuzzylogic.api.model.impl;

import com.castellanos94.fuzzylogic.api.model.Logic;
import com.castellanos94.fuzzylogic.api.model.Query;
import com.castellanos94.fuzzylogicgp.core.StateNode;
import com.castellanos94.fuzzylogicgp.core.TaskType;
import com.castellanos94.fuzzylogicgp.parser.ParserPredicate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Set;

public class EvaluationQuery extends Query {
    public EvaluationQuery() {
        this.job = TaskType.EVALUATION;
    }

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

    /**
     * Retorna la representacion actual del objecto segun la tarea de evaluacion
     *
     * @return NodeTree, si es nulo ocurrio un error en parser
     */
    @Override
    public com.castellanos94.fuzzylogicgp.core.NodeTree getPredicateTree() throws Exception {
        ParserPredicate parserPredicate = new ParserPredicate(this.predicate, convertStates(), new ArrayList<>());
        return parserPredicate.parser();
    }

    protected ArrayList<StateNode> convertStates() {
        ArrayList<StateNode> stateNodeArrayList = new ArrayList<>();
        states.stream().map(LinguisticState::toInternalObject).forEach(stateNodeArrayList::add);
        return stateNodeArrayList;
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
