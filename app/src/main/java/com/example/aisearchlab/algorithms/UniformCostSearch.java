package com.example.aisearchlab.algorithms;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class UniformCostSearch extends SearchAlgorithm {

    private PriorityQueue<SearchNode> openList;
    private Set<String> closedList;
    private Map<String, SearchNode> openListMap;

    @Override
    protected void onInitialize() {
        openList = new PriorityQueue<>();
        closedList = new HashSet<>();
        openListMap = new HashMap<>();
        
        SearchNode startNode = new SearchNode(initialState, null, "Start", 0, 0);
        openList.add(startNode);
        openListMap.put(initialState.getUniqueId(), startNode);
    }

    @Override
    public boolean step() {
        if (isFinished) return true;

        if (openList.isEmpty()) {
            isFinished = true; // Failure
            return true;
        }

        SearchNode current = openList.poll();
        openListMap.remove(current.state.getUniqueId());
        
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
            
            double gCost = current.pathCost + transition.cost;
            double hCost = 0; // UCS has no heuristic
            
            SearchNode childNode = new SearchNode(
                    transition.nextState,
                    current,
                    transition.action,
                    gCost,
                    hCost);

            SearchNode existingOpen = openListMap.get(childId);
            if (existingOpen == null || gCost < existingOpen.pathCost) {
                if (existingOpen != null) {
                    openList.remove(existingOpen);
                }
                openList.add(childNode);
                openListMap.put(childId, childNode);
            }
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
        return "Uniform Cost Search (UCS)";
    }
}
