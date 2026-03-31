package com.example.aisearchlab.algorithms;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class GreedyBestFirstSearch extends SearchAlgorithm {

    private PriorityQueue<SearchNode> openList;
    private Set<String> closedList;

    @Override
    protected void onInitialize() {
        // Queue ordered strictly by heuristic value
        openList = new PriorityQueue<>(Comparator.comparingDouble(node -> node.heuristicCost));
        closedList = new HashSet<>();
        
        SearchNode startNode = new SearchNode(initialState, null, "Start", 0, initialState.getHeuristic());
        openList.add(startNode);
    }

    @Override
    public boolean step() {
        if (isFinished) return true;

        if (openList.isEmpty()) {
            isFinished = true; // Failure
            return true;
        }

        SearchNode current = openList.poll();
        
        if (closedList.contains(current.state.getUniqueId())) {
            return false;
        }

        nodesExplored++;
        closedList.add(current.state.getUniqueId());

        if (current.state.isGoal()) {
            goalNode = current;
            isFinished = true;
            return true;
        }

        for (SearchState.Transition transition : current.state.getSuccessors()) {
            String childId = transition.nextState.getUniqueId();
            if (closedList.contains(childId)) continue;
            
            double hCost = transition.nextState.getHeuristic();
            
            SearchNode childNode = new SearchNode(
                    transition.nextState,
                    current,
                    transition.action,
                    current.pathCost + transition.cost, // still track path cost, but not used for prioritization
                    hCost);
            
            openList.add(childNode);
        }

        return false;
    }

    @Override
    public Collection<SearchNode> getOpenList() {
        return openList;
    }

    @Override
    public Set<String> getClosedList() {
        return closedList;
    }

    @Override
    public String getAlgorithmName() {
        return "Greedy Best-First Search";
    }
}
