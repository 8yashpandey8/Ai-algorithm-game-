package com.example.aisearchlab.games.waterjug;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.aisearchlab.ui.visualizer.SharedExplanationViewModel;

import com.example.aisearchlab.R;

public class WaterJugFragment extends Fragment {

    private WaterJugViewModel viewModel;

    private TextView tvMoves, tvStatus, tvTarget, tvInstruction;
    private TextView tvJugA, tvJugB, tvJugC;
    private View viewWaterA, viewWaterB, viewWaterC;
    private FrameLayout containerA, containerB, containerC;
    private Spinner spinnerDifficulty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water_jug, container, false);

        tvMoves = view.findViewById(R.id.tv_moves);
        tvStatus = view.findViewById(R.id.tv_status);
        tvTarget = view.findViewById(R.id.tv_target);
        tvInstruction = view.findViewById(R.id.tv_instruction);
        
        tvJugA = view.findViewById(R.id.tv_jug_a_level);
        tvJugB = view.findViewById(R.id.tv_jug_b_level);
        tvJugC = view.findViewById(R.id.tv_jug_c_level);
        
        viewWaterA = view.findViewById(R.id.view_water_a);
        viewWaterB = view.findViewById(R.id.view_water_b);
        viewWaterC = view.findViewById(R.id.view_water_c);
        
        containerA = view.findViewById(R.id.container_jug_a);
        containerB = view.findViewById(R.id.container_jug_b);
        containerC = view.findViewById(R.id.container_jug_c);
        
        spinnerDifficulty = view.findViewById(R.id.spinner_difficulty);

        setupClickListeners(view);
        setupSpinner();

        return view;
    }

    private void setupClickListeners(View view) {
        containerA.setOnClickListener(v -> viewModel.onJugClicked("A"));
        containerB.setOnClickListener(v -> viewModel.onJugClicked("B"));
        containerC.setOnClickListener(v -> viewModel.onJugClicked("C"));

        view.findViewById(R.id.btn_reset).setOnClickListener(v -> viewModel.resetToCurrentConfig());
        view.findViewById(R.id.btn_undo).setOnClickListener(v -> viewModel.undo());
        view.findViewById(R.id.btn_hint).setOnClickListener(v -> viewModel.solve());
        
        view.findViewById(R.id.btn_fill_selected).setOnClickListener(v -> {
            String selected = viewModel.selectedJug.getValue();
            if (selected != null) {
                viewModel.performAction("Fill Jug " + selected);
                viewModel.onJugClicked(selected); // Deselect
            } else {
                Toast.makeText(getContext(), "Select a jug first", Toast.LENGTH_SHORT).show();
            }
        });
        
        view.findViewById(R.id.btn_empty_selected).setOnClickListener(v -> {
            String selected = viewModel.selectedJug.getValue();
            if (selected != null) {
                viewModel.performAction("Empty Jug " + selected);
                viewModel.onJugClicked(selected); // Deselect
            } else {
                Toast.makeText(getContext(), "Select a jug first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        String[] levels = {"\uD83D\uDFE2 Easy (3L, 5L, 8L - Goal: 4L)", "\uD83D\uDFE1 Medium (4L, 7L, 10L - Goal: 5L)", "\uD83D\uDD34 Hard (6L, 9L, 20L - Goal: 7L)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, levels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapter);
        
        spinnerDifficulty.setSelection(1); // Medium default

        spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) viewModel.reset(3, 5, 8, 4);
                else if (position == 1) viewModel.reset(4, 7, 10, 5);
                else if (position == 2) viewModel.reset(6, 9, 20, 7);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(WaterJugViewModel.class);

        viewModel.currentState.observe(getViewLifecycleOwner(), state -> {
            tvJugA.setText(state.getJugAAmount() + " / " + state.getJugAMax() + "L");
            tvJugB.setText(state.getJugBAmount() + " / " + state.getJugBMax() + "L");
            tvJugC.setText(state.getJugCAmount() + " / " + state.getJugCMax() + "L");
            
            tvTarget.setText("Target: " + state.getTarget() + "L");

            int overallMax = Math.max(state.getJugAMax(), Math.max(state.getJugBMax(), state.getJugCMax()));
            adjustContainerHeight(containerA, state.getJugAMax(), overallMax);
            adjustContainerHeight(containerB, state.getJugBMax(), overallMax);
            adjustContainerHeight(containerC, state.getJugCMax(), overallMax);

            animateWaterLevel(viewWaterA, containerA, state.getJugAAmount(), state.getJugAMax());
            animateWaterLevel(viewWaterB, containerB, state.getJugBAmount(), state.getJugBMax());
            animateWaterLevel(viewWaterC, containerC, state.getJugCAmount(), state.getJugCMax());

            if (state.isGoal()) {
                tvStatus.setText("Solved! \uD83C\uDF89");
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
                tvInstruction.setText("Congratulations!");
            } else {
                tvStatus.setText("Playing");
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
            }
        });

        viewModel.moves.observe(getViewLifecycleOwner(), moves -> {
            tvMoves.setText(String.valueOf(moves));
        });

        viewModel.status.observe(getViewLifecycleOwner(), status -> {
            if ("Solving".equals(status)) {
                tvInstruction.setText("AI is solving...");
            }
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

        viewModel.selectedJug.observe(getViewLifecycleOwner(), selectedJug -> {
            highlightContainer(containerA, "A".equals(selectedJug));
            highlightContainer(containerB, "B".equals(selectedJug));
            highlightContainer(containerC, "C".equals(selectedJug));
            
            if (selectedJug != null) {
                tvInstruction.setText("Tap another jug to pour");
            } else {
                tvInstruction.setText("Tap a jug to pour");
            }
        });

        SharedExplanationViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedExplanationViewModel.class);
        viewModel.latestExplanation.observe(getViewLifecycleOwner(), exp -> {
            if (exp != null) {
                sharedViewModel.setExplanation(exp);
            }
        });
    }

    private void animateWaterLevel(View waterView, FrameLayout container, int amount, int max) {
        container.post(() -> {
            int containerHeight = container.getHeight();
            float ratio = (float) amount / max;
            int targetHeight = (int) (containerHeight * ratio);

            int currentHeight = waterView.getLayoutParams().height;
            if (currentHeight < 0) currentHeight = 0; // wrap_content/match_parent

            ValueAnimator animator = ValueAnimator.ofInt(currentHeight, targetHeight);
            animator.setDuration(300);
            animator.addUpdateListener(animation -> {
                waterView.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                waterView.requestLayout();
            });
            animator.start();
        });
    }

    private void adjustContainerHeight(FrameLayout container, int max, int overallMax) {
        int maxContainerHeightDp = 220; // Maximum allowed physical height
        int targetHeightDp = overallMax > 0 ? (int)(maxContainerHeightDp * ((float)max / overallMax)) : maxContainerHeightDp;
        if (targetHeightDp < 60) targetHeightDp = 60; // Minimum bound
        
        ViewGroup.LayoutParams params = container.getLayoutParams();
        params.height = (int)(targetHeightDp * getResources().getDisplayMetrics().density);
        container.setLayoutParams(params);
    }

    private void highlightContainer(FrameLayout container, boolean isSelected) {
        if (isSelected) {
            container.setAlpha(0.6f);
            container.setScaleX(1.05f);
            container.setScaleY(1.05f);
        } else {
            container.setAlpha(1.0f);
            container.setScaleX(1.0f);
            container.setScaleY(1.0f);
        }
    }

    private void showWinDialog() {
        if (getContext() == null || getView() == null) return;
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_win, null);
        builder.setView(dialogView);
        
        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.8f);
        }
        
        TextView tvStats = dialogView.findViewById(R.id.tv_win_stats);
        int moves = viewModel.moves.getValue() != null ? viewModel.moves.getValue() : 0;
        tvStats.setText("Moves: " + moves);
        
        dialogView.findViewById(R.id.btn_home).setOnClickListener(v -> {
            dialog.dismiss();
            androidx.navigation.Navigation.findNavController(requireView()).navigateUp();
        });
        
        dialogView.findViewById(R.id.btn_play_again).setOnClickListener(v -> {
            dialog.dismiss();
            viewModel.resetToCurrentConfig();
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
