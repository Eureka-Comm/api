package com.castellanos94.fuzzylogic.api.model.impl;

import com.castellanos94.fuzzylogicgp.core.DummyGenerator;
import com.castellanos94.fuzzylogicgp.core.NodeTree;
import com.castellanos94.fuzzylogicgp.parser.ParserPredicate;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Document(collection = "queries")

public class DiscoveryQuery extends EvaluationQuery {


    @NotNull
    @Min(value = 1)
    protected int populationSize;
    @NotNull
    @Min(value = 1)
    protected int numberOfResults;

    protected int numberOfIterations;
    @NotNull
    @Range(min = 0, max = 1)
    protected float mutationRate;
    @NotNull
    @Range(min = 0, max = 1)
    protected float minimumTruthValue;
    @NotNull
    @Min(value = 1)
    protected int adjPopulationSize;
    @NotNull
    @Min(value = 1)
    protected int adjNumberOfIterations;
    @NotNull
    @Range(min = 0, max = 1)
    protected float adjMinimumTruthValue;
    protected Set<Generator> generators;
    @NotNull
    @Range(min = 1000, max = 21600000)
    protected long maxTime;

    /**
     * Retorna un predicado en la estructura interna segun la tarea de descubrimiento
     *
     * @return null si ocurrio un error, de otra forma NodeTree
     */
    @Override
    public NodeTree getPredicateTree() throws Exception {
        ParserPredicate parserPredicate = new ParserPredicate(this.predicate, convertStates(), convertToDummyGenerator());

        return parserPredicate.parser();
    }

    /**
     * Transforma a una lista de dummy generatos
     *
     * @return dummygenerators
     */
    protected List<DummyGenerator> convertToDummyGenerator() {

        if (generators != null) {
            return generators.stream().map(Generator::toInternalObject).collect(Collectors.toList());
        }
        return List.of();
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(int numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    public void setNumberOfIterations(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
    }

    public float getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(float mutationRate) {
        this.mutationRate = mutationRate;
    }

    public float getMinimumTruthValue() {
        return minimumTruthValue;
    }

    public void setMinimumTruthValue(float minimumTruthValue) {
        this.minimumTruthValue = minimumTruthValue;
    }

    public int getAdjPopulationSize() {
        return adjPopulationSize;
    }

    public void setAdjPopulationSize(int adjPopulationSize) {
        this.adjPopulationSize = adjPopulationSize;
    }

    public int getAdjNumberOfIterations() {
        return adjNumberOfIterations;
    }

    public void setAdjNumberOfIterations(int adjNumberOfIterations) {
        this.adjNumberOfIterations = adjNumberOfIterations;
    }

    public float getAdjMinimumTruthValue() {
        return adjMinimumTruthValue;
    }

    public void setAdjMinimumTruthValue(float adjMinimumTruthValue) {
        this.adjMinimumTruthValue = adjMinimumTruthValue;
    }

    public Set<Generator> getGenerators() {
        return generators;
    }

    public void setGenerators(Set<Generator> generators) {
        this.generators = generators;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    public String toString() {
        return "DiscoveryQuery{" +
                "name='" + name + '\'' +
                ", tags=" + tags +
                ", isPublic=" + isPublic +
                ", populationSize=" + populationSize +
                ", numberOfResults=" + numberOfResults +
                ", numberOfIterations=" + numberOfIterations +
                ", mutationRate=" + mutationRate +
                ", minimumTruthValue=" + minimumTruthValue +
                ", adjPopulationSize=" + adjPopulationSize +
                ", adjNumberOfIterations=" + adjNumberOfIterations +
                ", adjMinimumTruthValue=" + adjMinimumTruthValue +
                ", generators=" + generators +
                ", description='" + description + '\'' +
                ", states=" + states +
                ", logic=" + logic +
                ", predicate='" + predicate + '\'' +
                ", maxTime='" + maxTime + '\'' +
                '}';
    }
}
