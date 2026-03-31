package com.example.aisearchlab.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchNode implements Comparable<SearchNode> {
    public final SearchState state;
    public final SearchNode parent;
    public final String action;
    public final double pathCost;
    public final double heuristicCost;
    public final double totalCost;

    public SearchNode(SearchState state, SearchNode parent, String action, double pathCost, double heuristicCost) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.pathCost = pathCost;
        this.heuristicCost = heuristicCost;
        this.totalCost = pathCost + heuristicCost;
    }

    public List<SearchNode> getPathFromRoot() {
        List<SearchNode> path = new ArrayList<>();
        SearchNode current = this;
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    @Override
    public int compareTo(SearchNode other) {
        return Double.compare(this.totalCost, other.totalCost);
    }
}
