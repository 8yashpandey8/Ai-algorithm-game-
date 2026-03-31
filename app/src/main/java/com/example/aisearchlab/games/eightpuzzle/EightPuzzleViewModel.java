package com.example.aisearchlab.games.eightpuzzle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aisearchlab.algorithms.SearchNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EightPuzzleViewModel extends ViewModel {

    private final MutableLiveData<EightPuzzleState> _currentState = new MutableLiveData<>();
    public LiveData<EightPuzzleState> currentState = _currentState;

    private final MutableLiveData<com.example.aisearchlab.models.AlgorithmExplanation> _latestExplanation = new MutableLiveData<>();
    public LiveData<com.example.aisearchlab.models.AlgorithmExplanation> latestExplanation = _latestExplanation;

    private final MutableLiveData<Integer> _moves = new MutableLiveData<>(0);
    public LiveData<Integer> moves = _moves;
    
    // Status can be: "Playing", "Solving", "Solved"
    private final MutableLiveData<String> _status = new MutableLiveData<>("Playing");
    public LiveData<String> status = _status;

    private final MutableLiveData<Integer> _timeElapsed = new MutableLiveData<>(0);
    public LiveData<Integer> timeElapsed = _timeElapsed;

    private final MutableLiveData<Integer> _minMoves = new MutableLiveData<>(-1);
    public LiveData<Integer> minMoves = _minMoves;

    private Timer timer;
    private boolean timerRunning = false;

    public EightPuzzleViewModel() {
        // Initialize with a simple unsolved state
        int[][] startBoard = {
            {1, 2, 3},
            {4, 0, 6},
            {7, 5, 8}
        };
        EightPuzzleState state = new EightPuzzleState(startBoard);
        _currentState.setValue(state);
        calculateMinMovesInBackground(state);
    }

    public void shuffle() {
        // Perform random valid moves
        int[][] board = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };
        EightPuzzleState state = new EightPuzzleState(board);
        
        for (int i = 0; i < 50; i++) {
            List<com.example.aisearchlab.algorithms.SearchState.Transition> successors = state.getSuccessors();
            int randomIdx = (int) (Math.random() * successors.size());
            state = (EightPuzzleState) successors.get(randomIdx).nextState;
        }
        
        _currentState.setValue(state);
        _moves.setValue(0);
        _status.setValue("Playing");
        _minMoves.setValue(-1);
        stopTimer();
        _timeElapsed.setValue(0);
        calculateMinMovesInBackground(state);
    }

    public void tryMove(int row, int col) {
        if ("Solving".equals(_status.getValue()) || "Solved".equals(_status.getValue())) {
            return;
        }

        EightPuzzleState state = _currentState.getValue();
        if (state == null) return;
        
        // Start timer on first move
        if (!timerRunning && (_moves.getValue() == null || _moves.getValue() == 0)) {
            startTimer();
        }
        
        int[][] board = state.getBoard();
        
        // Find empty cell
        int eRow = -1, eCol = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    eRow = i;
                    eCol = j;
                    break;
                }
            }
        }
        
        // Check if clicked tile is adjacent to empty tile
        if ((Math.abs(eRow - row) == 1 && eCol == col) || (Math.abs(eCol - col) == 1 && eRow == row)) {
            int[][] newBoard = new int[3][3];
            for(int i=0; i<3; i++) System.arraycopy(board[i], 0, newBoard[i], 0, 3);
            
            newBoard[eRow][eCol] = newBoard[row][col];
            newBoard[row][col] = 0;
            
            EightPuzzleState nextState = new EightPuzzleState(newBoard);
            _currentState.setValue(nextState);
            _moves.setValue((_moves.getValue() == null ? 0 : _moves.getValue()) + 1);
            
            if (nextState.isGoal()) {
                _status.setValue("Solved");
                stopTimer();
            }
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

    private void calculateMinMovesInBackground(EightPuzzleState state) {
        new Thread(() -> {
            com.example.aisearchlab.algorithms.AStarSearch aStar = new com.example.aisearchlab.algorithms.AStarSearch();
            aStar.initialize(state);
            while (!aStar.isFinished()) {
                aStar.step();
            }
            SearchNode goal = aStar.getGoalNode();
            if (goal != null) {
                // Number of moves is path size - 1
                _minMoves.postValue(goal.getPathFromRoot().size() - 1);
            }
        }).start();
    }

    public void solve() {
        if ("Solving".equals(_status.getValue()) || "Solved".equals(_status.getValue())) {
            return;
        }
        
        _status.setValue("Solving");
        EightPuzzleState startState = _currentState.getValue();
        
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            com.example.aisearchlab.algorithms.AStarSearch aStar = new com.example.aisearchlab.algorithms.AStarSearch();
            aStar.initialize(startState);
            while (!aStar.isFinished()) {
                aStar.step();
            }
            long endTime = System.currentTimeMillis();
            
            com.example.aisearchlab.models.AlgorithmExplanation explanation = new com.example.aisearchlab.models.AlgorithmExplanation("8 Puzzle", aStar.getAlgorithmName());
            explanation.timeTakenMs = endTime - startTime;
            explanation.steps = aStar.getExplanationSteps();
            explanation.isSolved = aStar.getGoalNode() != null;
            _latestExplanation.postValue(explanation);
            
            SearchNode goal = aStar.getGoalNode();
            if (goal != null) {
                List<SearchNode> path = goal.getPathFromRoot();
                int currentMoves = _moves.getValue() == null ? 0 : _moves.getValue();
                
                // Animate the path
                for (int i = 1; i < path.size(); i++) {
                    EightPuzzleState nextState = (EightPuzzleState) path.get(i).state;
                    try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }
                    
                    _currentState.postValue(nextState);
                    _moves.postValue(currentMoves + i);
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
