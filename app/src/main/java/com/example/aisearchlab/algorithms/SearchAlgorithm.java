package com.example.aisearchlab.algorithms;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import com.example.aisearchlab.models.AlgorithmStep;

public abstract class SearchAlgorithm {
    protected SearchState initialState;
    protected SearchNode goalNode;
    protected boolean isFinished;
    protected int nodesExplored;
    protected List<AlgorithmStep> explanationSteps;

    public void initialize(SearchState initialState) {
        this.initialState = initialState;
        this.goalNode = null;
        this.isFinished = false;
        this.nodesExplored = 0;
        this.explanationSteps = new ArrayList<>();
        onInitialize();
    }

    public List<AlgorithmStep> getExplanationSteps() {
        return explanationSteps;
    }

    protected abstract void onInitialize();
    
    // Executes one step of the algorithm. Returns true if finished (goal found or failed).
    public abstract boolean step();

    public boolean isFinished() {
        return isFinished;
    }

    public SearchNode getGoalNode() {
        return goalNode;
    }

    public int getNodesExplored() {
        return nodesExplored;
    }

    // For Teacher Mode / Visualization
    public abstract Collection<SearchNode> getOpenList();
    public abstract Set<String> getClosedList();
    
    public abstract String getAlgorithmName();
}
