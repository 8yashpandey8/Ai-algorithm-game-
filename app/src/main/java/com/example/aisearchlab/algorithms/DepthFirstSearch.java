package com.example.aisearchlab.algorithms;

import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import com.example.aisearchlab.models.AlgorithmStep;

public class DepthFirstSearch extends SearchAlgorithm {

    private Stack<SearchNode> openList;
    private Set<String> closedList;

    @Override
    protected void onInitialize() {
        openList = new Stack<>();
        closedList = new HashSet<>();
        
        SearchNode startNode = new SearchNode(initialState, null, "Start", 0, 0);
        openList.push(startNode);
    }

    @Override
    public boolean step() {
        if (isFinished) return true;

        if (openList.isEmpty()) {
            isFinished = true; // Failure
            return true;
        }

        SearchNode current = openList.pop();
        nodesExplored++;
        closedList.add(current.state.getUniqueId());

        if (current.state.isGoal()) {
            goalNode = current;
            isFinished = true;
            explanationSteps.add(new AlgorithmStep(current.state, new ArrayList<>(), "Goal!", "Target reached.", false));
            return true;
        }

        List<String> options = new ArrayList<>();
        boolean hasChildren = false;

        if (current.state instanceof com.example.aisearchlab.games.nqueens.NQueensState) {
            com.example.aisearchlab.games.nqueens.NQueensState nqs = (com.example.aisearchlab.games.nqueens.NQueensState) current.state;
            int n = nqs.getN();
            int[] q = nqs.getQueens();
            int nextRow = -1;
            for (int i = 0; i < n; i++) {
                if (q[i] == -1) {
                    nextRow = i;
                    break;
                }
            }

            if (nextRow != -1) {
                options.add("Trying Row " + nextRow + ":");
                for (int col = 0; col < n; col++) {
                    boolean safe = true;
                    String conflictReason = "";
                    for (int i = 0; i < nextRow; i++) {
                        if (q[i] == col) { safe = false; conflictReason = "Same column as row " + i; break; }
                        if (Math.abs(q[i] - col) == Math.abs(i - nextRow)) { safe = false; conflictReason = "Diagonal conflict with row " + i; break; }
                    }
                    if (safe) {
                        options.add("→ Try Col " + col + " (Valid)");
                        hasChildren = true;
                        
                        int[] newQ = new int[n];
                        System.arraycopy(q, 0, newQ, 0, n);
                        newQ[nextRow] = col;
                        com.example.aisearchlab.games.nqueens.NQueensState nextState = new com.example.aisearchlab.games.nqueens.NQueensState(newQ);
                        openList.push(new SearchNode(nextState, current, "Place Queen at (" + nextRow + ", " + col + ")", current.pathCost + 1, 0));
                    } else {
                        options.add("→ Try Col " + col + " (Conflict: " + conflictReason + ")");
                    }
                }
            }
        } else {
            for (SearchState.Transition transition : current.state.getSuccessors()) {
                String childId = transition.nextState.getUniqueId();
                if (!closedList.contains(childId) && !isNodeInOpenList(childId)) {
                    SearchNode childNode = new SearchNode(
                            transition.nextState,
                            current,
                            transition.action,
                            current.pathCost + transition.cost,
                            0);
                    openList.push(childNode);
                    options.add("→ " + transition.action + " (Valid)");
                    hasChildren = true;
                } else {
                    options.add("→ " + transition.action + " (Conflict/Visited)");
                }
            }
        }
        
        SearchNode next = openList.empty() ? null : openList.peek();
        String chosen = next != null ? "→ " + next.action : "None";
        String reason = hasChildren ? "DFS explores depth-first (Stack LIFO)" : "No valid moves. Backtracking...";
        
        explanationSteps.add(new AlgorithmStep(current.state, options, chosen, reason, !hasChildren));

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
        return "Depth-First Search (DFS)";
    }
}
