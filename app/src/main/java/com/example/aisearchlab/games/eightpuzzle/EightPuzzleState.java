package com.example.aisearchlab.games.eightpuzzle;

import com.example.aisearchlab.algorithms.SearchState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EightPuzzleState implements SearchState {

    private final int[][] board;
    private final int emptyRow;
    private final int emptyCol;

    private static final int[][] GOAL_STATE = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 0}
    };

    public EightPuzzleState(int[][] board) {
        this.board = new int[3][3];
        int eRow = -1, eCol = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.board[i][j] = board[i][j];
                if (board[i][j] == 0) {
                    eRow = i;
                    eCol = j;
                }
            }
        }
        this.emptyRow = eRow;
        this.emptyCol = eCol;
    }

    public int[][] getBoard() {
        return board;
    }

    @Override
    public boolean isGoal() {
        return Arrays.deepEquals(board, GOAL_STATE);
    }

    @Override
    public List<Transition> getSuccessors() {
        List<Transition> successors = new ArrayList<>();

        // Possible moves: Up, Down, Left, Right (meaning moving the empty blank space)
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        String[] actions = {"Up", "Down", "Left", "Right"};

        for (int i = 0; i < directions.length; i++) {
            int newRow = emptyRow + directions[i][0];
            int newCol = emptyCol + directions[i][1];

            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                int[][] newBoard = copyBoard(board);
                // Swap empty space with the neighboring tile
                newBoard[emptyRow][emptyCol] = newBoard[newRow][newCol];
                newBoard[newRow][newCol] = 0;

                EightPuzzleState nextState = new EightPuzzleState(newBoard);
                successors.add(new Transition(nextState, actions[i], 1.0));
            }
        }

        return successors;
    }

    @Override
    public String getUniqueId() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(board[i][j]);
            }
        }
        return sb.toString();
    }

    @Override
    public int getHeuristic() {
        // Manhattan Distance
        int dist = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int value = board[i][j];
                if (value != 0) {
                    // Goal positions 
                    // 1: (0,0), 2: (0,1), 3: (0,2)
                    // 4: (1,0), 5: (1,1), 6: (1,2)
                    // 7: (2,0), 8: (2,1)
                    int targetRow = (value - 1) / 3;
                    int targetCol = (value - 1) % 3;
                    dist += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                }
            }
        }
        return dist;
    }

    private int[][] copyBoard(int[][] source) {
        int[][] dest = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(source[i], 0, dest[i], 0, 3);
        }
        return dest;
    }
}
