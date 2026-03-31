package com.example.aisearchlab.models;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmExplanation {
    public String gameName;
    public String algorithmName;
    public long timeTakenMs;
    public boolean isSolved;
    public List<AlgorithmStep> steps;

    public AlgorithmExplanation(String gameName, String algorithmName) {
        this.gameName = gameName;
        this.algorithmName = algorithmName;
        this.steps = new ArrayList<>();
    }
}
