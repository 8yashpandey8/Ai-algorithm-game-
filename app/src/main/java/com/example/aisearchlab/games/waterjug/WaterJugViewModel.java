package com.example.aisearchlab.games.waterjug;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Stack;

public class WaterJugViewModel extends ViewModel {

    private final MutableLiveData<WaterJugState> _currentState = new MutableLiveData<>();
    public LiveData<WaterJugState> currentState = _currentState;

    private final MutableLiveData<com.example.aisearchlab.models.AlgorithmExplanation> _latestExplanation = new MutableLiveData<>();
    public LiveData<com.example.aisearchlab.models.AlgorithmExplanation> latestExplanation = _latestExplanation;

    private final MutableLiveData<Integer> _moves = new MutableLiveData<>(0);
    public LiveData<Integer> moves = _moves;

    private final MutableLiveData<String> _status = new MutableLiveData<>("Playing");
    public LiveData<String> status = _status;
    
    private final MutableLiveData<String> _selectedJug = new MutableLiveData<>(null);
    public LiveData<String> selectedJug = _selectedJug;

    private final Stack<WaterJugState> history = new Stack<>();

    public WaterJugViewModel() {
        reset(8, 5, 3, 4); // Default to a standard 3 jug problem, e.g. 8L, 5L, 3L and target 4L
    }

    public void reset(int maxA, int maxB, int maxC, int target) {
        history.clear();
        WaterJugState initialState = new WaterJugState(0, 0, 0, maxA, maxB, maxC, target);
        _currentState.setValue(initialState);
        _moves.setValue(0);
        _status.setValue("Playing");
        _selectedJug.setValue(null);
    }
    
    public void resetToCurrentConfig() {
        WaterJugState current = _currentState.getValue();
        if (current != null) {
            reset(current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
        }
    }

    public void undo() {
        if (!history.isEmpty()) {
            WaterJugState previous = history.pop();
            _currentState.setValue(previous);
            _moves.setValue(Math.max(0, (_moves.getValue() == null ? 0 : _moves.getValue()) - 1));
            _status.setValue("Playing");
            _selectedJug.setValue(null);
        }
    }

    public void onJugClicked(String jugName) {
        if ("Solved".equals(_status.getValue())) return;
        
        String currentSelected = _selectedJug.getValue();
        
        if (currentSelected == null) {
            // First tap: select source jug
            _selectedJug.setValue(jugName);
        } else if (currentSelected.equals(jugName)) {
            // Tap same jug: deselect
            _selectedJug.setValue(null);
        } else {
            // Second tap: pour selected -> target
            String actionName = "Pour " + currentSelected + " -> " + jugName;
            performAction(actionName);
            _selectedJug.setValue(null);
        }
    }

    public void performAction(String action) {
        if ("Solved".equals(_status.getValue())) return;

        WaterJugState current = _currentState.getValue();
        if (current == null) return;

        WaterJugState nextState = null;

        switch (action) {
            case "Fill Jug A":
                nextState = new WaterJugState(current.getJugAMax(), current.getJugBAmount(), current.getJugCAmount(), current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
            case "Fill Jug B":
                nextState = new WaterJugState(current.getJugAAmount(), current.getJugBMax(), current.getJugCAmount(), current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
            case "Fill Jug C":
                nextState = new WaterJugState(current.getJugAAmount(), current.getJugBAmount(), current.getJugCMax(), current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
            case "Empty Jug A":
                nextState = new WaterJugState(0, current.getJugBAmount(), current.getJugCAmount(), current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
            case "Empty Jug B":
                nextState = new WaterJugState(current.getJugAAmount(), 0, current.getJugCAmount(), current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
            case "Empty Jug C":
                nextState = new WaterJugState(current.getJugAAmount(), current.getJugBAmount(), 0, current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
            case "Pour A -> B":
                int pourAB = Math.min(current.getJugAAmount(), current.getJugBMax() - current.getJugBAmount());
                nextState = new WaterJugState(current.getJugAAmount() - pourAB, current.getJugBAmount() + pourAB, current.getJugCAmount(), current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
            case "Pour A -> C":
                int pourAC = Math.min(current.getJugAAmount(), current.getJugCMax() - current.getJugCAmount());
                nextState = new WaterJugState(current.getJugAAmount() - pourAC, current.getJugBAmount(), current.getJugCAmount() + pourAC, current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
            case "Pour B -> A":
                int pourBA = Math.min(current.getJugBAmount(), current.getJugAMax() - current.getJugAAmount());
                nextState = new WaterJugState(current.getJugAAmount() + pourBA, current.getJugBAmount() - pourBA, current.getJugCAmount(), current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
            case "Pour B -> C":
                int pourBC = Math.min(current.getJugBAmount(), current.getJugCMax() - current.getJugCAmount());
                nextState = new WaterJugState(current.getJugAAmount(), current.getJugBAmount() - pourBC, current.getJugCAmount() + pourBC, current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
            case "Pour C -> A":
                int pourCA = Math.min(current.getJugCAmount(), current.getJugAMax() - current.getJugAAmount());
                nextState = new WaterJugState(current.getJugAAmount() + pourCA, current.getJugBAmount(), current.getJugCAmount() - pourCA, current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
            case "Pour C -> B":
                int pourCB = Math.min(current.getJugCAmount(), current.getJugBMax() - current.getJugBAmount());
                nextState = new WaterJugState(current.getJugAAmount(), current.getJugBAmount() + pourCB, current.getJugCAmount() - pourCB, current.getJugAMax(), current.getJugBMax(), current.getJugCMax(), current.getTarget());
                break;
        }

        if (nextState != null && !nextState.equals(current)) {
            history.push(current);
            _currentState.setValue(nextState);
            _moves.setValue((_moves.getValue() == null ? 0 : _moves.getValue()) + 1);
            if (nextState.isGoal()) {
                _status.setValue("Solved");
            }
        }
    }

    public void solve() {
        if ("Solving".equals(_status.getValue()) || "Solved".equals(_status.getValue())) {
            return;
        }
        
        _status.setValue("Solving");
        WaterJugState startState = _currentState.getValue();
        
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            com.example.aisearchlab.algorithms.BreadthFirstSearch bfs = new com.example.aisearchlab.algorithms.BreadthFirstSearch();
            bfs.initialize(startState);
            while (!bfs.isFinished()) {
                bfs.step();
            }
            long endTime = System.currentTimeMillis();
            
            com.example.aisearchlab.models.AlgorithmExplanation explanation = new com.example.aisearchlab.models.AlgorithmExplanation("Water Jug", bfs.getAlgorithmName());
            explanation.timeTakenMs = endTime - startTime;
            explanation.steps = bfs.getExplanationSteps();
            explanation.isSolved = bfs.getGoalNode() != null;
            _latestExplanation.postValue(explanation);
            
            com.example.aisearchlab.algorithms.SearchNode goal = bfs.getGoalNode();
            if (goal != null) {
                java.util.List<com.example.aisearchlab.algorithms.SearchNode> path = goal.getPathFromRoot();
                int currentMoves = _moves.getValue() == null ? 0 : _moves.getValue();
                
                // Animate the path
                for (int i = 1; i < path.size(); i++) {
                    WaterJugState nextState = (WaterJugState) path.get(i).state;
                    try { Thread.sleep(600); } catch (InterruptedException e) { e.printStackTrace(); }
                    
                    _currentState.postValue(nextState);
                    _moves.postValue(currentMoves + i);
                }
                _status.postValue("Solved");
            } else {
                _status.postValue("Failed To Solve");
            }
        }).start();
    }
}
