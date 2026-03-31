package com.example.aisearchlab.games.nqueens;

import com.example.aisearchlab.algorithms.SearchState;

import java.util.ArrayList;
import java.util.List;

public class NQueensState implements SearchState {

    private final int[] queens; // index is row, value is column where queen is placed (-1 if no queen in row)
    private final int n;

    public NQueensState(int n) {
        this.n = n;
        this.queens = new int[n];
        for (int i = 0; i < n; i++) queens[i] = -1;
    }

    public NQueensState(int[] queens) {
        this.n = queens.length;
        this.queens = new int[n];
        System.arraycopy(queens, 0, this.queens, 0, n);
    }

    public int[] getQueens() {
        return queens;
    }

    public int getN() {
        return n;
    }

    @Override
    public boolean isGoal() {
        // Goal if all rows have queens (since we only generate valid successors)
        // AND all placed queens are safe (redundant if generation is safe, but good for custom boards)
        for (int i = 0; i < n; i++) {
            if (queens[i] == -1) return false;
        }
        return getConflicts() == 0;
    }

    // Number of attacking pairs
    public int getConflicts() {
        int conflicts = 0;
        for (int i = 0; i < n; i++) {
            if (queens[i] == -1) continue;
            for (int j = i + 1; j < n; j++) {
                if (queens[j] == -1) continue;
                if (queens[i] == queens[j]) conflicts++; // Same column
                if (Math.abs(queens[i] - queens[j]) == Math.abs(i - j)) conflicts++; // Same diagonal
            }
        }
        return conflicts;
    }

    @Override
    public List<Transition> getSuccessors() {
        List<Transition> successors = new ArrayList<>();
        
        // Find first empty row
        int nextRow = -1;
        for (int i = 0; i < n; i++) {
            if (queens[i] == -1) {
                nextRow = i;
                break;
            }
        }
        
        if (nextRow == -1) return successors; // Board full

        // Try placing a queen in each column of the next row safely
        for (int col = 0; col < n; col++) {
            if (isSafe(nextRow, col)) {
                int[] newQueens = new int[n];
                System.arraycopy(queens, 0, newQueens, 0, n);
                newQueens[nextRow] = col;
                
                NQueensState nextState = new NQueensState(newQueens);
                successors.add(new Transition(nextState, "Place Queen at (" + nextRow + ", " + col + ")", 1.0));
            }
        }

        return successors;
    }

    private boolean isSafe(int row, int col) {
        for (int i = 0; i < row; i++) {
            if (queens[i] == col) return false; // Same column
            if (Math.abs(queens[i] - col) == Math.abs(i - row)) return false; // Same diagonal
        }
        return true;
    }

    @Override
    public String getUniqueId() {
        StringBuilder sb = new StringBuilder();
        for (int q : queens) sb.append(q).append(",");
        return sb.toString();
    }

    @Override
    public int getHeuristic() {
        // Heuristic for A* or Greedy. Usually N-Queens is solved with Backtracking (DFS).
        // For Hill Climbing, the heuristic is the number of conflicts.
        return getConflicts();
    }
}
