package com.example.aisearchlab.ui.compare;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.aisearchlab.algorithms.SearchAlgorithm;

public class CompareViewModel extends AndroidViewModel {

    private final MutableLiveData<String> _status1 = new MutableLiveData<>("Waiting");
    public LiveData<String> status1 = _status1;

    private final MutableLiveData<String> _metrics1 = new MutableLiveData<>("Nodes Explored: 0\nPath Cost: 0\nMax Depth: 0");
    public LiveData<String> metrics1 = _metrics1;

    private final MutableLiveData<String> _status2 = new MutableLiveData<>("Waiting");
    public LiveData<String> status2 = _status2;

    private final MutableLiveData<String> _metrics2 = new MutableLiveData<>("Nodes Explored: 0\nPath Cost: 0\nMax Depth: 0");
    public LiveData<String> metrics2 = _metrics2;

    private final MutableLiveData<String> _winner = new MutableLiveData<>("");
    public LiveData<String> winner = _winner;

    private final MutableLiveData<Boolean> _isRunning = new MutableLiveData<>(false);
    public LiveData<Boolean> isRunning = _isRunning;

    public CompareViewModel(@NonNull Application application) {
        super(application);
    }

    public void runComparison(SearchAlgorithm algo1, SearchAlgorithm algo2) {
        _isRunning.setValue(true);
        _winner.setValue("");

        _status1.setValue("Running...");
        _status2.setValue("Running...");
        
        _metrics1.setValue("Nodes Explored: 0\nPath Cost: 0\nMax Depth: 0");
        _metrics2.setValue("Nodes Explored: 0\nPath Cost: 0\nMax Depth: 0");

        Thread t1 = new Thread(() -> runAlgorithm(algo1, 1));
        Thread t2 = new Thread(() -> runAlgorithm(algo2, 2));

        t1.start();
        t2.start();
        
        // Setup a monitor thread
        new Thread(() -> {
            try {
                t1.join();
                t2.join();
                
                // Both finished
                _isRunning.postValue(false);
                determineWinner(algo1, algo2);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void runAlgorithm(SearchAlgorithm algo, int id) {
        long startTime = System.currentTimeMillis();
        
        while (!algo.isFinished()) {
            algo.step();
            
            // Post occasional updates
            if (algo.getNodesExplored() % 500 == 0) {
                updateMetrics(algo, id, "Running...");
            }
        }
        
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;
        
        String finalStatus = algo.getGoalNode() != null ? "Solved in " + timeTaken + "ms" : "Failed";
        updateMetrics(algo, id, finalStatus);
    }

    private void updateMetrics(SearchAlgorithm algo, int id, String status) {
        String metricsText = "Nodes Explored: " + algo.getNodesExplored() + "\n";
        if (algo.getGoalNode() != null) {
            metricsText += "Path Cost: " + algo.getGoalNode().pathCost;
        } else {
            metricsText += "Path Cost: N/A";
        }

        if (id == 1) {
            _metrics1.postValue(metricsText);
            _status1.postValue(status);
        } else {
            _metrics2.postValue(metricsText);
            _status2.postValue(status);
        }
    }

    private void determineWinner(SearchAlgorithm algo1, SearchAlgorithm algo2) {
        boolean solved1 = algo1.getGoalNode() != null;
        boolean solved2 = algo2.getGoalNode() != null;

        if (solved1 && !solved2) {
            _winner.postValue("Algorithm 1 Wins! (Alg 2 Failed)");
        } else if (!solved1 && solved2) {
            _winner.postValue("Algorithm 2 Wins! (Alg 1 Failed)");
        } else if (!solved1 && !solved2) {
            _winner.postValue("Draw! Both failed.");
        } else {
            // Both solved, compare nodes explored
            if (algo1.getNodesExplored() < algo2.getNodesExplored()) {
                _winner.postValue("Algorithm 1 Wins! (Fewer nodes explored)");
            } else if (algo2.getNodesExplored() < algo1.getNodesExplored()) {
                _winner.postValue("Algorithm 2 Wins! (Fewer nodes explored)");
            } else {
                _winner.postValue("Draw! Both explored the exact same number of nodes.");
            }
        }
    }
}
