package com.example.aisearchlab.algorithms;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import com.example.aisearchlab.models.AlgorithmStep;

public class AStarSearch extends SearchAlgorithm {

    private PriorityQueue<SearchNode> openList;
    private Set<String> closedList;
    private Map<String, SearchNode> openListMap;

    @Override
    protected void onInitialize() {
        openList = new PriorityQueue<>();
        closedList = new HashSet<>();
        openListMap = new HashMap<>();
        
        SearchNode startNode = new SearchNode(initialState, null, "Start", 0, initialState.getHeuristic());
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
            return false; // Skip if already expanded (due to duplicated entries in PriorityQueue)
        }

        nodesExplored++;
        closedList.add(current.state.getUniqueId());

        if (current.state.isGoal()) {
            goalNode = current;
            isFinished = true;
            explanationSteps.add(new AlgorithmStep(current.state, new ArrayList<>(), "Goal!", "Target reached.", false));
            return true;
        }

        List<String> options = new ArrayList<>();
        for (SearchState.Transition transition : current.state.getSuccessors()) {
            String childId = transition.nextState.getUniqueId();
            if (closedList.contains(childId)) {
                options.add("→ " + transition.action + " (Skipped: Closed)");
                continue;
            }
            
            double gCost = current.pathCost + transition.cost;
            double hCost = transition.nextState.getHeuristic();
            double fCost = gCost + hCost;
            options.add("→ " + transition.action + " (cost: " + fCost + ")");
            
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
        
        SearchNode peek = openList.peek();
        String chosen = peek != null ? "→ " + peek.action + " (f=" + (peek.pathCost + peek.state.getHeuristic()) + ")" : "None";
        String reason = "Minimum cost (f = g + h)";
        
        explanationSteps.add(new AlgorithmStep(current.state, options, chosen, reason, false));

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
        return "A* Search";
    }
}
