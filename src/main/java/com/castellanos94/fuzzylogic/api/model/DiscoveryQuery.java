package com.castellanos94.fuzzylogic.api.model;

import com.castellanos94.fuzzylogic.api.model.impl.Generator;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class DiscoveryQuery extends EvaluationQuery {

    protected ArrayList<Generator> generators;

    @NotNull
    @Min(value = 1)
    protected int pop_size;
    @NotNull
    @Min(value = 1)
    protected int num_results;
    @NotNull
    @Min(value = 1)
    protected int num_iterations;
    @NotNull
    @Range(min = 0, max = 1)
    protected float mutation_percentage;
    @NotNull
    @Range(min = 0, max = 1)
    protected float min_truth_value;
    @NotNull
    @Min(value = 1)
    protected int adj_pop_size;
    @NotNull
    @Min(value = 1)
    protected int adj_num_iter;
    @NotNull
    @Range(min = 0, max = 1)
    protected float adj_min_truth_value;

    public ArrayList<Generator> getGenerators() {
        return generators;
    }

    public void setGenerators(ArrayList<Generator> generators) {
        this.generators = generators;
    }

    public int getPop_size() {
        return pop_size;
    }

    public void setPop_size(int pop_size) {
        this.pop_size = pop_size;
    }

    public int getNum_results() {
        return num_results;
    }

    public void setNum_results(int num_results) {
        this.num_results = num_results;
    }

    public int getNum_iterations() {
        return num_iterations;
    }

    public void setNum_iterations(int num_iterations) {
        this.num_iterations = num_iterations;
    }

    public float getMutation_percentage() {
        return mutation_percentage;
    }

    public void setMutation_percentage(float mutation_percentage) {
        this.mutation_percentage = mutation_percentage;
    }

    public float getMin_truth_value() {
        return min_truth_value;
    }

    public void setMin_truth_value(float min_truth_value) {
        this.min_truth_value = min_truth_value;
    }

    public int getAdj_pop_size() {
        return adj_pop_size;
    }

    public void setAdj_pop_size(int adj_pop_size) {
        this.adj_pop_size = adj_pop_size;
    }

    public int getAdj_num_iter() {
        return adj_num_iter;
    }

    public void setAdj_num_iter(int adj_num_iter) {
        this.adj_num_iter = adj_num_iter;
    }

    public float getAdj_min_truth_value() {
        return adj_min_truth_value;
    }

    public void setAdj_min_truth_value(float adj_min_truth_value) {
        this.adj_min_truth_value = adj_min_truth_value;
    }

    @Override
    public String toString() {
        return "DiscoveryQuery{" +
                "generators=" + generators +
                ", pop_size=" + pop_size +
                ", num_results=" + num_results +
                ", num_iterations=" + num_iterations +
                ", mutation_percentage=" + mutation_percentage +
                ", min_truth_value=" + min_truth_value +
                ", adj_pop_size=" + adj_pop_size +
                ", adj_num_iter=" + adj_num_iter +
                ", adj_min_truth_value=" + adj_min_truth_value +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dataset='" + dataset + '\'' +
                ", states=" + states +
                ", logicType=" + logicType +
                ", predicate='" + predicate + '\'' +
                '}';
    }
}
