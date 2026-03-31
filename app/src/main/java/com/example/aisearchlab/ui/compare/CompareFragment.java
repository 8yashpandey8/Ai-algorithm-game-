package com.example.aisearchlab.ui.compare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.aisearchlab.R;
import com.example.aisearchlab.algorithms.AStarSearch;
import com.example.aisearchlab.algorithms.BreadthFirstSearch;
import com.example.aisearchlab.algorithms.DepthFirstSearch;
import com.example.aisearchlab.algorithms.GreedyBestFirstSearch;
import com.example.aisearchlab.algorithms.HillClimbing;
import com.example.aisearchlab.algorithms.SearchAlgorithm;
import com.example.aisearchlab.algorithms.SearchState;
import com.example.aisearchlab.algorithms.UniformCostSearch;
import com.example.aisearchlab.games.eightpuzzle.EightPuzzleState;
import com.example.aisearchlab.games.nqueens.NQueensState;
import com.example.aisearchlab.games.waterjug.WaterJugState;
import com.google.android.material.button.MaterialButton;

public class CompareFragment extends Fragment {

    private CompareViewModel viewModel;
    private Spinner spinnerGame, spinnerAlgo1, spinnerAlgo2;
    private MaterialButton btnStartRace;
    private TextView tvStatus1, tvMetrics1, tvStatus2, tvMetrics2, tvWinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare, container, false);
        
        spinnerGame = view.findViewById(R.id.spinner_compare_game);
        spinnerAlgo1 = view.findViewById(R.id.spinner_algo1);
        spinnerAlgo2 = view.findViewById(R.id.spinner_algo2);
        btnStartRace = view.findViewById(R.id.btn_start_race);
        
        tvStatus1 = view.findViewById(R.id.tv_status1);
        tvMetrics1 = view.findViewById(R.id.tv_metrics1);
        tvStatus2 = view.findViewById(R.id.tv_status2);
        tvMetrics2 = view.findViewById(R.id.tv_metrics2);
        tvWinner = view.findViewById(R.id.tv_winner);
        
        setupSpinners();
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CompareViewModel.class);

        viewModel.status1.observe(getViewLifecycleOwner(), status -> tvStatus1.setText(status));
        viewModel.metrics1.observe(getViewLifecycleOwner(), metrics -> tvMetrics1.setText(metrics));
        
        viewModel.status2.observe(getViewLifecycleOwner(), status -> tvStatus2.setText(status));
        viewModel.metrics2.observe(getViewLifecycleOwner(), metrics -> tvMetrics2.setText(metrics));
        
        viewModel.winner.observe(getViewLifecycleOwner(), winner -> tvWinner.setText(winner));
        
        viewModel.isRunning.observe(getViewLifecycleOwner(), isRunning -> {
            btnStartRace.setEnabled(!isRunning);
            if (isRunning) {
                btnStartRace.setText("Racing...");
            } else {
                btnStartRace.setText("Start Race");
            }
        });

        btnStartRace.setOnClickListener(v -> startComparison());
    }

    private void setupSpinners() {
        String[] games = {"8 Puzzle", "Water Jug", "N Queens"};
        ArrayAdapter<String> gameAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, games);
        gameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGame.setAdapter(gameAdapter);

        String[] algorithms = {"Breadth-First Search (BFS)", "Depth-First Search (DFS)", "Uniform Cost Search (UCS)",
                               "A* Search", "Greedy Best-First", "Hill Climbing"};
        
        ArrayAdapter<String> algoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, algorithms);
        algoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spinnerAlgo1.setAdapter(algoAdapter);
        spinnerAlgo2.setAdapter(algoAdapter);
        
        // Set different defaults
        spinnerAlgo1.setSelection(0); // BFS
        spinnerAlgo2.setSelection(3); // A*
    }

    private void startComparison() {
        String selectedGame = (String) spinnerGame.getSelectedItem();
        String selectedAlgo1 = (String) spinnerAlgo1.getSelectedItem();
        String selectedAlgo2 = (String) spinnerAlgo2.getSelectedItem();

        SearchState initialState1 = getInitialStateForGame(selectedGame);
        SearchState initialState2 = getInitialStateForGame(selectedGame); // need two separate instances

        SearchAlgorithm algo1 = getAlgorithmForName(selectedAlgo1);
        SearchAlgorithm algo2 = getAlgorithmForName(selectedAlgo2);

        if (initialState1 != null && initialState2 != null && algo1 != null && algo2 != null) {
            algo1.initialize(initialState1);
            algo2.initialize(initialState2);
            viewModel.runComparison(algo1, algo2);
        } else {
            Toast.makeText(getContext(), "Error initializing comparison.", Toast.LENGTH_SHORT).show();
        }
    }

    private SearchState getInitialStateForGame(String game) {
        switch (game) {
            case "8 Puzzle":
                int[][] initialBoard = {{1, 2, 3}, {4, 5, 0}, {7, 8, 6}}; 
                return new EightPuzzleState(initialBoard);
            case "Water Jug":
                return new WaterJugState(0, 0, 0, 8, 5, 3, 4); 
            case "N Queens":
                return new NQueensState(8);
            default:
                return null;
        }
    }

    private SearchAlgorithm getAlgorithmForName(String name) {
        if (name.contains("BFS")) return new BreadthFirstSearch();
        if (name.contains("DFS")) return new DepthFirstSearch();
        if (name.contains("UCS")) return new UniformCostSearch();
        if (name.contains("A*")) return new AStarSearch();
        if (name.contains("Greedy")) return new GreedyBestFirstSearch();
        if (name.contains("Hill Climbing")) return new HillClimbing();
        return null;
    }
}
