package com.example.aisearchlab.games.eightpuzzle;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import com.example.aisearchlab.ui.visualizer.SharedExplanationViewModel;
import com.example.aisearchlab.ui.visualizer.SharedExplanationViewModel;

import com.example.aisearchlab.R;

public class EightPuzzleFragment extends Fragment {

    private EightPuzzleViewModel viewModel;
    private GridLayout puzzleGrid;
    private TextView tvMoves;
    private TextView tvStatus;
    private TextView tvTimer;
    private TextView tvMinMoves;
    private TextView[][] tiles = new TextView[3][3];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eight_puzzle, container, false);
        
        puzzleGrid = view.findViewById(R.id.puzzle_grid);
        tvMoves = view.findViewById(R.id.tv_moves);
        tvStatus = view.findViewById(R.id.tv_status);
        tvTimer = view.findViewById(R.id.tv_timer);
        tvMinMoves = view.findViewById(R.id.tv_min_moves);
        
        view.findViewById(R.id.btn_shuffle).setOnClickListener(v -> viewModel.shuffle());
        view.findViewById(R.id.btn_solve).setOnClickListener(v -> {
            viewModel.solve();
        });

        initializeGrid();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EightPuzzleViewModel.class);

        viewModel.currentState.observe(getViewLifecycleOwner(), state -> {
            updateGrid(state.getBoard());
            if (state.isGoal()) {
                tvStatus.setText("Solved!");
                tvStatus.setTextColor(Color.GREEN);
            } else {
                tvStatus.setText("Playing");
                tvStatus.setTextColor(Color.GRAY);
            }
        });

        viewModel.moves.observe(getViewLifecycleOwner(), moves -> {
            tvMoves.setText("Moves: " + moves);
        });
        
        viewModel.status.observe(getViewLifecycleOwner(), status -> {
            tvStatus.setText(status);
            View btnVisualize = view.findViewById(R.id.btn_visualize);
            if (btnVisualize != null) {
                btnVisualize.setVisibility("Solved".equals(status) ? View.VISIBLE : View.GONE);
                btnVisualize.setOnClickListener(v -> {
                    androidx.navigation.Navigation.findNavController(view).navigate(R.id.navigation_visualizer);
                });
            }
            if ("Solved".equals(status) && getView() != null) {
                showWinDialog();
            }
        });

        viewModel.timeElapsed.observe(getViewLifecycleOwner(), time -> {
            long minutes = time / 60;
            long seconds = time % 60;
            tvTimer.setText(String.format("Time: %02d:%02d", minutes, seconds));
        });

        viewModel.minMoves.observe(getViewLifecycleOwner(), minMoves -> {
            if (minMoves >= 0) {
                tvMinMoves.setText("Min Moves: " + minMoves);
            } else {
                tvMinMoves.setText("Min Moves: --");
            }
        });

        SharedExplanationViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedExplanationViewModel.class);
        viewModel.latestExplanation.observe(getViewLifecycleOwner(), exp -> {
            if (exp != null) {
                sharedViewModel.setExplanation(exp);
            }
        });
    }

    private void initializeGrid() {
        puzzleGrid.removeAllViews();
        int tileSize = (int) (getResources().getDisplayMetrics().density * 80);
        int margin = (int) (getResources().getDisplayMetrics().density * 4);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                TextView tile = new TextView(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = tileSize;
                params.height = tileSize;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                params.setMargins(margin, margin, margin, margin);
                
                tile.setLayoutParams(params);
                tile.setGravity(Gravity.CENTER);
                tile.setTextSize(32);
                tile.setTextColor(Color.WHITE);
                
                final int row = i;
                final int col = j;
                tile.setOnClickListener(v -> viewModel.tryMove(row, col));
                
                puzzleGrid.addView(tile);
                tiles[i][j] = tile;
            }
        }
    }

    private void updateGrid(int[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int val = board[i][j];
                TextView tile = tiles[i][j];
                if (val == 0) {
                    tile.setText("");
                    tile.setBackgroundResource(R.drawable.tile_empty_background);
                } else {
                    tile.setText(String.valueOf(val));
                    tile.setBackgroundResource(R.drawable.tile_background);
                }
            }
        }
    }

    private void showWinDialog() {
        if (getContext() == null || getView() == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_win, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.8f);
        }
        
        TextView tvStats = dialogView.findViewById(R.id.tv_win_stats);
        long time = viewModel.timeElapsed.getValue() != null ? viewModel.timeElapsed.getValue() : 0;
        int moves = viewModel.moves.getValue() != null ? viewModel.moves.getValue() : 0;
        tvStats.setText("Moves: " + moves + " | Time: " + time + "s");
        
        dialogView.findViewById(R.id.btn_home).setOnClickListener(v -> {
            dialog.dismiss();
            androidx.navigation.Navigation.findNavController(requireView()).navigateUp();
        });
        
        dialogView.findViewById(R.id.btn_play_again).setOnClickListener(v -> {
            dialog.dismiss();
            viewModel.shuffle();
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
