package com.example.aisearchlab.models;

import java.util.List;

public class AlgorithmStep {
    public Object state;
    public List<String> options;
    public String chosen;
    public String reason;
    public boolean isBacktracking;

    public AlgorithmStep(Object state, List<String> options, String chosen, String reason, boolean isBacktracking) {
        this.state = state;
        this.options = options;
        this.chosen = chosen;
        this.reason = reason;
        this.isBacktracking = isBacktracking;
    }
}
