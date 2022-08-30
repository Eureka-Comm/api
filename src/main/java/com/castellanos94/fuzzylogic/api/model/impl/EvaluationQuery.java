package com.castellanos94.fuzzylogic.api.model.impl;

import java.util.ArrayList;

import org.springframework.data.mongodb.core.mapping.Document;

import com.castellanos94.fuzzylogic.api.model.Query;
import com.castellanos94.fuzzylogicgp.core.TaskType;
import com.castellanos94.fuzzylogicgp.parser.ParserPredicate;
@Document(collection = "queries")
public class EvaluationQuery extends Query {
    public EvaluationQuery() {
        this.job = TaskType.EVALUATION;
    }

    protected boolean includeFuzzyData;

    public boolean isIncludeFuzzyData() {
        return includeFuzzyData;
    }

    public void setIncludeFuzzyData(boolean includeFuzzyData) {
        this.includeFuzzyData = includeFuzzyData;
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
