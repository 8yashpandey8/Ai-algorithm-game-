package com.example.aisearchlab.games.nqueens;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.aisearchlab.ui.visualizer.SharedExplanationViewModel;
import com.example.aisearchlab.ui.visualizer.SharedExplanationViewModel;

import com.example.aisearchlab.R;

public class NQueensFragment extends Fragment {

    private NQueensViewModel viewModel;
    private GridLayout chessboardGrid;
    private TextView tvConflicts;
    private TextView tvStatus;
    private TextView tvTimer;
    
    private TextView[][] tiles;
    private int currentN = 8;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_n_queens, container, false);
        
        chessboardGrid = view.findViewById(R.id.chessboard_grid);
        tvConflicts = view.findViewById(R.id.tv_conflicts);
        tvStatus = view.findViewById(R.id.tv_status);
        tvTimer = view.findViewById(R.id.tv_timer);
        
        view.findViewById(R.id.btn_reset).setOnClickListener(v -> viewModel.resetBoard(8));
        view.findViewById(R.id.btn_solve).setOnClickListener(v -> viewModel.solve());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NQueensViewModel.class);

        viewModel.boardSize.observe(getViewLifecycleOwner(), n -> {
            if (n != null && n != currentN || tiles == null) {
                currentN = n;
                initializeBoard(n);
            }
        });

        viewModel.currentState.observe(getViewLifecycleOwner(), state -> {
            updateBoard(state.getQueens());
            if (state.isGoal()) {
                tvStatus.setText("Solved! All Safe.");
                tvStatus.setTextColor(Color.GREEN);
            } else {
                tvStatus.setText("Playing");
                tvStatus.setTextColor(Color.GRAY);
            }
        });

        viewModel.conflicts.observe(getViewLifecycleOwner(), conflicts -> {
            tvConflicts.setText("Conflicts: " + conflicts);
            if (conflicts > 0) {
                tvConflicts.setTextColor(Color.RED);
            } else {
                tvConflicts.setTextColor(Color.BLACK); // Use default or theme color properly later
            }
        });
        
        viewModel.status.observe(getViewLifecycleOwner(), status -> {
            tvStatus.setText(status);
            View btnVisualize = view.findViewById(R.id.btn_visualize);
            if (btnVisualize != null) {
                // Check if status contains "Solved" to match NQueens "Solved! All Safe."
                boolean solved = status != null && status.contains("Solved");
                btnVisualize.setVisibility(solved ? View.VISIBLE : View.GONE);
                btnVisualize.setOnClickListener(v -> {
                    androidx.navigation.Navigation.findNavController(view).navigate(R.id.navigation_visualizer);
                });
            }
            
            if (status != null && status.contains("Solved") && getView() != null && getContext() != null) {
                showWinDialog();
            }
        });

        viewModel.timeElapsed.observe(getViewLifecycleOwner(), time -> {
            long minutes = time / 60;
            long seconds = time % 60;
            tvTimer.setText(String.format("Time: %02d:%02d", minutes, seconds));
        });

        SharedExplanationViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedExplanationViewModel.class);
        viewModel.latestExplanation.observe(getViewLifecycleOwner(), exp -> {
            if (exp != null) {
                sharedViewModel.setExplanation(exp);
            }
        });
    }

    private void initializeBoard(int n) {
        chessboardGrid.removeAllViews();
        chessboardGrid.setRowCount(n);
        chessboardGrid.setColumnCount(n);
        tiles = new TextView[n][n];
        
        // Calculate cell size based on screen width
        int displayWidth = getResources().getDisplayMetrics().widthPixels - (int)(48 * getResources().getDisplayMetrics().density); // Padding margin
        int cellSize = displayWidth / n;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                TextView cell = new TextView(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                
                cell.setLayoutParams(params);
                cell.setGravity(Gravity.CENTER);
                cell.setTextSize(cellSize / 3);
                
                boolean isLightSquare = (i + j) % 2 == 0;
                cell.setBackgroundColor(isLightSquare ? Color.parseColor("#F0D9B5") : Color.parseColor("#B58863"));
                
                final int r = i;
                final int c = j;
                cell.setOnClickListener(v -> viewModel.toggleQueen(r, c));
                
                chessboardGrid.addView(cell);
                tiles[i][j] = cell;
            }
        }
    }

    private void updateBoard(int[] queens) {
        if (tiles == null) return;
        
        int n = queens.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j].setText("");
            }
            if (queens[i] != -1) {
                tiles[i][queens[i]].setText("\u265B"); // Unicode for Black Queen
                tiles[i][queens[i]].setTextColor(Color.BLACK);
            }
        }
    }

    private void showWinDialog() {
        if (getContext() == null || getView() == null) return;
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_win, null);
        builder.setView(dialogView);
        
        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.8f);
        }
        
        TextView tvStats = dialogView.findViewById(R.id.tv_win_stats);
        long time = viewModel.timeElapsed.getValue() != null ? viewModel.timeElapsed.getValue() : 0;
        int conflicts = viewModel.conflicts.getValue() != null ? viewModel.conflicts.getValue() : 0;
        tvStats.setText("Conflicts: " + conflicts + " | Time: " + time + "s");
        
        dialogView.findViewById(R.id.btn_home).setOnClickListener(v -> {
            dialog.dismiss();
            androidx.navigation.Navigation.findNavController(requireView()).navigateUp();
        });
        
        dialogView.findViewById(R.id.btn_play_again).setOnClickListener(v -> {
            dialog.dismiss();
            if (viewModel.boardSize.getValue() != null) {
                viewModel.resetBoard(viewModel.boardSize.getValue());
            }
        });
        
        View btnVisualize = dialogView.findViewById(R.id.btn_visualize);
        btnVisualize.setVisibility(View.VISIBLE);
        btnVisualize.setOnClickListener(v -> {
            dialog.dismiss();
            androidx.navigation.Navigation.findNavController(requireView()).navigate(R.id.navigation_visualizer);
        });
        
        dialog.setOnShowListener(d -> {
            dialogView.setScaleX(0.8f);
            dialogView.setScaleY(0.8f);
            dialogView.setAlpha(0f);
            dialogView.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).start();
        });
        
        dialog.show();
    }
}
