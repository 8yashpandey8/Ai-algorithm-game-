package com.example.aisearchlab.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HillClimbing extends SearchAlgorithm {

    private SearchNode currentNode;
    private Set<String> closedList;

    @Override
    protected void onInitialize() {
        closedList = new HashSet<>();
        currentNode = new SearchNode(initialState, null, "Start", 0, initialState.getHeuristic());
    }

    @Override
    public boolean step() {
        if (isFinished) return true;

        if (currentNode == null) {
            isFinished = true; // Failure (reached local optima without finding goal)
            return true;
        }

        nodesExplored++;
        closedList.add(currentNode.state.getUniqueId());

        if (currentNode.state.isGoal()) {
            goalNode = currentNode;
            isFinished = true;
            return true;
        }

        // Generate successors
        List<SearchState.Transition> successors = currentNode.state.getSuccessors();
        
        SearchNode bestNeighbor = null;
        double minHeuristic = currentNode.heuristicCost; // Strictly better (steepest ascent/descent)
        
        for (SearchState.Transition transition : successors) {
            double hCost = transition.nextState.getHeuristic();
            if (hCost < minHeuristic) {
                minHeuristic = hCost;
                bestNeighbor = new SearchNode(
                        transition.nextState,
                        currentNode,
                        transition.action,
                        currentNode.pathCost + transition.cost,
                        hCost
                );
            }
        }
        
        if (bestNeighbor == null) {
            // Local maxima reached. Stop algorithm.
            isFinished = true;
            return true;
        }

        currentNode = bestNeighbor;
        return false;

    }

    @Override
    public Collection<SearchNode> getOpenList() {
        // Technically just the current node, or empty
        List<SearchNode> open = new ArrayList<>();
        if (currentNode != null) open.add(currentNode);
        return open;
    }

    @Override
    public Set<String> getClosedList() {
        return closedList;
    }

    @Override
    public String getAlgorithmName() {
        return "Hill Climbing";
    }
}
