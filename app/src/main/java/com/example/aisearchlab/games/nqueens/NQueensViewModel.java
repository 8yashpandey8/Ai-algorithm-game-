package com.example.aisearchlab.games.nqueens;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class NQueensViewModel extends ViewModel {

    private final MutableLiveData<NQueensState> _currentState = new MutableLiveData<>();
    public LiveData<NQueensState> currentState = _currentState;

    private final MutableLiveData<com.example.aisearchlab.models.AlgorithmExplanation> _latestExplanation = new MutableLiveData<>();
    public LiveData<com.example.aisearchlab.models.AlgorithmExplanation> latestExplanation = _latestExplanation;

    private final MutableLiveData<Integer> _boardSize = new MutableLiveData<>(8);
    public LiveData<Integer> boardSize = _boardSize;

    private final MutableLiveData<String> _status = new MutableLiveData<>("Playing");
    public LiveData<String> status = _status;

    private final MutableLiveData<Integer> _conflicts = new MutableLiveData<>(0);
    public LiveData<Integer> conflicts = _conflicts;

    private final MutableLiveData<Integer> _timeElapsed = new MutableLiveData<>(0);
    public LiveData<Integer> timeElapsed = _timeElapsed;

    private Timer timer;
    private boolean timerRunning = false;

    public NQueensViewModel() {
        resetBoard(8);
    }

    public void resetBoard(int n) {
        _boardSize.setValue(n);
        NQueensState ns = new NQueensState(n);
        _currentState.setValue(ns);
        _conflicts.setValue(ns.getConflicts());
        _status.setValue("Playing");
        stopTimer();
        _timeElapsed.setValue(0);
    }

    public void toggleQueen(int row, int col) {
        if ("Solved".equals(_status.getValue())) return;
        
        NQueensState current = _currentState.getValue();
        if (current == null) return;

        if (!timerRunning) {
            startTimer();
        }
        
        int[] queens = current.getQueens();
        int[] newQueens = new int[queens.length];
        System.arraycopy(queens, 0, newQueens, 0, queens.length);
        
        // If clicking on an existing queen, remove it
        if (newQueens[row] == col) {
            newQueens[row] = -1;
        } else {
            // Place new queen (overrides existing in that row)
            newQueens[row] = col;
        }
        
        NQueensState nextState = new NQueensState(newQueens);
        _currentState.setValue(nextState);
        _conflicts.setValue(nextState.getConflicts());
        
        if (nextState.isGoal()) {
            _status.setValue("Solved");
            stopTimer();
        } else {
            _status.setValue("Playing");
        }
    }

    private void startTimer() {
        if (timer != null) timer.cancel();
        timer = new Timer();
        timerRunning = true;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Integer current = _timeElapsed.getValue();
                _timeElapsed.postValue(current == null ? 1 : current + 1);
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timerRunning = false;
    }

    public void solve() {
        if ("Solving".equals(_status.getValue()) || "Solved".equals(_status.getValue())) {
            return;
        }

        _status.setValue("Solving");
        NQueensState startState = _currentState.getValue();

        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            com.example.aisearchlab.algorithms.DepthFirstSearch dfs = new com.example.aisearchlab.algorithms.DepthFirstSearch();
            dfs.initialize(startState);
            while (!dfs.isFinished()) {
                dfs.step();
            }
            long endTime = System.currentTimeMillis();
            
            com.example.aisearchlab.models.AlgorithmExplanation explanation = new com.example.aisearchlab.models.AlgorithmExplanation("N-Queens", dfs.getAlgorithmName());
            explanation.timeTakenMs = endTime - startTime;
            explanation.steps = dfs.getExplanationSteps();
            explanation.isSolved = dfs.getGoalNode() != null;
            _latestExplanation.postValue(explanation);

            com.example.aisearchlab.algorithms.SearchNode goal = dfs.getGoalNode();
            if (goal != null) {
                java.util.List<com.example.aisearchlab.algorithms.SearchNode> path = goal.getPathFromRoot();

                // Animate the path
                for (int i = 1; i < path.size(); i++) {
                    NQueensState nextState = (NQueensState) path.get(i).state;
                    try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

                    _currentState.postValue(nextState);
                    _conflicts.postValue(nextState.getConflicts());
                }
                _status.postValue("Solved");
            } else {
                _status.postValue("Failed To Solve");
            }
        }).start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopTimer();
    }
}
