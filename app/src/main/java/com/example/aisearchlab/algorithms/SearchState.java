package com.example.aisearchlab.algorithms;

import java.util.List;

public interface SearchState {
    boolean isGoal();
    List<Transition> getSuccessors();
    String getUniqueId();
    int getHeuristic(); // For informed searches

    class Transition {
        public final SearchState nextState;
        public final String action;
        public final double cost;

        public Transition(SearchState nextState, String action, double cost) {
            this.nextState = nextState;
            this.action = action;
            this.cost = cost;
        }
    }
}
