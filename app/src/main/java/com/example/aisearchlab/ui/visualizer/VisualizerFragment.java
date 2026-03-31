package com.example.aisearchlab.ui.visualizer;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.aisearchlab.R;
import com.example.aisearchlab.games.eightpuzzle.EightPuzzleState;
import com.example.aisearchlab.games.nqueens.NQueensState;
import com.example.aisearchlab.games.waterjug.WaterJugState;
import com.example.aisearchlab.models.AlgorithmStep;
import com.google.android.material.button.MaterialButton;

public class VisualizerFragment extends Fragment {

    private VisualizerViewModel viewModel;
    private SharedExplanationViewModel sharedViewModel;

    private TextView tvAlgoName, tvStepCount, tvTimeTaken;
    private FrameLayout flGameStateContainer;
    private TextView tvOptions, tvChosen, tvReason;
    private MaterialButton btnPrev, btnPlayPause, btnNext, btnSpeed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visualizer, container, false);
        
        tvAlgoName = view.findViewById(R.id.tv_algo_name);
        tvStepCount = view.findViewById(R.id.tv_step_count);
        tvTimeTaken = view.findViewById(R.id.tv_time_taken);
        flGameStateContainer = view.findViewById(R.id.fl_game_state_container);
        tvOptions = view.findViewById(R.id.tv_options);
        tvChosen = view.findViewById(R.id.tv_chosen);
        tvReason = view.findViewById(R.id.tv_reason);
        
        btnPrev = view.findViewById(R.id.btn_prev);
        btnPlayPause = view.findViewById(R.id.btn_play_pause);
        btnNext = view.findViewById(R.id.btn_next);
        btnSpeed = view.findViewById(R.id.btn_speed);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(VisualizerViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedExplanationViewModel.class);

        sharedViewModel.explanation.observe(getViewLifecycleOwner(), exp -> {
            if (exp != null) {
                viewModel.setExplanation(exp);
            }
        });

        viewModel.gameAlgoName.observe(getViewLifecycleOwner(), tvAlgoName::setText);
        viewModel.stepCountMetrics.observe(getViewLifecycleOwner(), tvStepCount::setText);
        viewModel.timeMetrics.observe(getViewLifecycleOwner(), tvTimeTaken::setText);

        viewModel.isPlaying.observe(getViewLifecycleOwner(), isPlaying -> {
            btnPlayPause.setIconResource(isPlaying ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
            btnPlayPause.setText(isPlaying ? "Pause" : "Play");
        });

        viewModel.playbackSpeed.observe(getViewLifecycleOwner(), speed -> {
            btnSpeed.setText(speed + "x");
        });

        viewModel.currentStep.observe(getViewLifecycleOwner(), step -> {
            if (step != null) {
                updateThinkingPanel(step);
                flGameStateContainer.post(() -> drawGameState(step.state));
            }
        });

        btnPlayPause.setOnClickListener(v -> viewModel.playPause());
        btnNext.setOnClickListener(v -> viewModel.nextStep());
        btnPrev.setOnClickListener(v -> viewModel.prevStep());
        btnSpeed.setOnClickListener(v -> viewModel.toggleSpeed());
    }

    private void updateThinkingPanel(AlgorithmStep step) {
        StringBuilder optionsStr = new StringBuilder();
        for (String opt : step.options) {
            optionsStr.append(opt).append("\n");
        }
        tvOptions.setText(optionsStr.toString().trim());
        
        tvChosen.setText(step.chosen);
        tvReason.setText(step.reason);
        
        if (step.isBacktracking || step.chosen.contains("None") || step.chosen.contains("Reject") || step.chosen.contains("Conflict")) {
            tvChosen.setBackgroundResource(R.drawable.rounded_panel_error_bg);
            tvChosen.setTextColor(android.graphics.Color.WHITE);
        } else {
            tvChosen.setBackgroundResource(R.drawable.rounded_panel_bg);
            tvChosen.setTextColor(android.graphics.Color.parseColor("#1DB954"));
        }
    }

    private void drawGameState(Object state) {
        if (state instanceof EightPuzzleState) {
            drawEightPuzzle((EightPuzzleState) state);
        } else if (state instanceof WaterJugState) {
            drawWaterJug((WaterJugState) state);
        } else if (state instanceof NQueensState) {
            drawNQueens((NQueensState) state);
        }
    }

    private void drawEightPuzzle(EightPuzzleState state) {
        flGameStateContainer.removeAllViews();
        GridLayout grid = new GridLayout(getContext());
        grid.setRowCount(3);
        grid.setColumnCount(3);
        
        int w = flGameStateContainer.getWidth();
        int h = flGameStateContainer.getHeight();
        int size = Math.min(w, h) / 3 - 16;
        if (size <= 0) size = 150;
        
        int[][] board = state.getBoard();
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                TextView tv = new TextView(getContext());
                tv.setTextSize(32);
                tv.setGravity(android.view.Gravity.CENTER);
                tv.setTextColor(android.graphics.Color.WHITE);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = size;
                params.height = size;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                params.setMargins(8,8,8,8);
                tv.setLayoutParams(params);
                
                if (board[i][j] == 0) {
                    tv.setBackgroundResource(com.example.aisearchlab.R.drawable.tile_empty_background);
                } else {
                    tv.setText(String.valueOf(board[i][j]));
                    tv.setBackgroundResource(com.example.aisearchlab.R.drawable.tile_background);
                }
                grid.addView(tv);
            }
        }
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(-2, -2);
        flp.gravity = android.view.Gravity.CENTER;
        flGameStateContainer.addView(grid, flp);
    }

    private void drawNQueens(NQueensState state) {
        flGameStateContainer.removeAllViews();
        int n = state.getN();
        GridLayout grid = new GridLayout(getContext());
        grid.setRowCount(n);
        grid.setColumnCount(n);
        
        int size = Math.min(flGameStateContainer.getWidth(), flGameStateContainer.getHeight()) / n;
        if (size <= 0) size = 50;
        
        int[] queens = state.getQueens();
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                TextView tv = new TextView(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = size;
                params.height = size;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                tv.setLayoutParams(params);
                tv.setGravity(android.view.Gravity.CENTER);
                tv.setTextSize(size / 3f);
                
                boolean isLight = (i + j) % 2 == 0;
                tv.setBackgroundColor(isLight ? android.graphics.Color.parseColor("#F0D9B5") : android.graphics.Color.parseColor("#B58863"));
                
                if (queens[i] == j) {
                    tv.setText("\u265B");
                    tv.setTextColor(android.graphics.Color.BLACK);
                }
                grid.addView(tv);
            }
        }
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(-2, -2);
        flp.gravity = android.view.Gravity.CENTER;
        flGameStateContainer.addView(grid, flp);
    }

    private void drawWaterJug(WaterJugState state) {
        flGameStateContainer.removeAllViews();
        android.widget.LinearLayout row = new android.widget.LinearLayout(getContext());
        row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        row.setWeightSum(3);
        row.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        
        int overallMax = Math.max(state.getJugAMax(), Math.max(state.getJugBMax(), state.getJugCMax()));
        
        row.addView(createJugView("Jug A", state.getJugAAmount(), state.getJugAMax(), overallMax));
        row.addView(createJugView("Jug B", state.getJugBAmount(), state.getJugBMax(), overallMax));
        row.addView(createJugView("Jug C", state.getJugCAmount(), state.getJugCMax(), overallMax));
        
        row.setGravity(android.view.Gravity.CENTER);
        flGameStateContainer.addView(row);
    }

    private android.view.View createJugView(String name, int amount, int max, int overallMax) {
        android.widget.LinearLayout col = new android.widget.LinearLayout(getContext());
        col.setOrientation(android.widget.LinearLayout.VERTICAL);
        col.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -1, 1));
        col.setGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);
        col.setPadding(8,8,8,8);
        
        TextView title = new TextView(getContext());
        title.setText(name + "\n" + amount + "/" + max + "L");
        title.setGravity(android.view.Gravity.CENTER);
        title.setTextSize(16);
        title.setTextColor(android.graphics.Color.DKGRAY);
        col.addView(title);
        
        int maxJugHeightDp = 160;
        int targetHeightDp = overallMax > 0 ? (int)(maxJugHeightDp * ((float)max / overallMax)) : maxJugHeightDp;
        if (targetHeightDp < 60) targetHeightDp = 60;
        
        FrameLayout jugBody = new FrameLayout(getContext());
        jugBody.setBackgroundColor(android.graphics.Color.LTGRAY);
        android.widget.LinearLayout.LayoutParams p = new android.widget.LinearLayout.LayoutParams(
                (int)(80 * getResources().getDisplayMetrics().density), 
                (int)(targetHeightDp * getResources().getDisplayMetrics().density));
        p.topMargin = 16;
        jugBody.setLayoutParams(p);
        
        android.view.View water = new android.view.View(getContext());
        water.setBackgroundColor(android.graphics.Color.parseColor("#1DB954"));
        FrameLayout.LayoutParams wp = new FrameLayout.LayoutParams(-1, -1);
        wp.gravity = android.view.Gravity.BOTTOM;
        wp.height = max == 0 ? 0 : (int)(targetHeightDp * getResources().getDisplayMetrics().density * ((float)amount/max));
        water.setLayoutParams(wp);
        
        jugBody.addView(water);
        col.addView(jugBody);
        return col;
    }
}
