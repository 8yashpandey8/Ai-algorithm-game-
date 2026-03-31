package com.example.aisearchlab.algorithms;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;
import com.example.aisearchlab.models.AlgorithmStep;

public class BreadthFirstSearch extends SearchAlgorithm {

    private Queue<SearchNode> openList;
    private Set<String> closedList;

    @Override
    protected void onInitialize() {
        openList = new LinkedList<>();
        closedList = new HashSet<>();
        
        SearchNode startNode = new SearchNode(initialState, null, "Start", 0, 0);
        openList.add(startNode);
        
        // In BFS, early goal test is optimal, but for visualization of standard graph search
        // we might do late goal test. We will use late goal test for simplicity.
    }

    @Override
    public boolean step() {
        if (isFinished) return true;

        if (openList.isEmpty()) {
            isFinished = true; // Failure
            return true;
        }

        SearchNode current = openList.poll();
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
            if (!closedList.contains(childId) && !isNodeInOpenList(childId)) {
                SearchNode childNode = new SearchNode(
                        transition.nextState,
                        current,
                        transition.action,
                        current.pathCost + transition.cost,
                        0);
                openList.add(childNode);
                options.add("→ " + transition.action + " \u2192 " + childId);
            } else {
                options.add("→ " + transition.action + " (Skipped, already seen)");
            }
        }
        
        SearchNode next = openList.peek();
        String chosen = next != null ? next.state.getUniqueId() : "None";
        String reason = "BFS explores level-wise (Queue FIFO)";
        
        explanationSteps.add(new AlgorithmStep(current.state, options, chosen, reason, false));

        return false;
    }

    private boolean isNodeInOpenList(String id) {
        for (SearchNode node : openList) {
            if (node.state.getUniqueId().equals(id)) return true;
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
        return "Breadth-First Search (BFS)";
    }
}
