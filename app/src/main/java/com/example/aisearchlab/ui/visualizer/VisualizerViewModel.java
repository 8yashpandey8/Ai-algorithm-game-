package com.example.aisearchlab.ui.visualizer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aisearchlab.models.AlgorithmExplanation;
import com.example.aisearchlab.models.AlgorithmStep;

public class VisualizerViewModel extends ViewModel {

    private AlgorithmExplanation explanation;
    
    // UI state
    private final MutableLiveData<String> _gameAlgoName = new MutableLiveData<>("");
    public LiveData<String> gameAlgoName = _gameAlgoName;

    private final MutableLiveData<String> _stepCountMetrics = new MutableLiveData<>("Step: 0 / 0");
    public LiveData<String> stepCountMetrics = _stepCountMetrics;

    private final MutableLiveData<String> _timeMetrics = new MutableLiveData<>("0ms");
    public LiveData<String> timeMetrics = _timeMetrics;

    private final MutableLiveData<AlgorithmStep> _currentStep = new MutableLiveData<>();
    public LiveData<AlgorithmStep> currentStep = _currentStep;

    private final MutableLiveData<Boolean> _isPlaying = new MutableLiveData<>(false);
    public LiveData<Boolean> isPlaying = _isPlaying;
    
    private final MutableLiveData<Float> _playbackSpeed = new MutableLiveData<>(1.0f);
    public LiveData<Float> playbackSpeed = _playbackSpeed;

    private int currentIndex = 0;
    private Thread playbackThread;

    public void setExplanation(AlgorithmExplanation exp) {
        this.explanation = exp;
        this.currentIndex = 0;
        
        if (exp != null) {
            _gameAlgoName.setValue(exp.gameName + " - " + exp.algorithmName);
            _timeMetrics.setValue(exp.timeTakenMs + "ms");
            updateUIState();
        }
    }

    public void playPause() {
        if (Boolean.TRUE.equals(_isPlaying.getValue())) {
            _isPlaying.setValue(false);
            if (playbackThread != null) playbackThread.interrupt();
        } else {
            _isPlaying.setValue(true);
            startPlayback();
        }
    }

    private void startPlayback() {
        playbackThread = new Thread(() -> {
            while (explanation != null && currentIndex < explanation.steps.size() - 1 && Boolean.TRUE.equals(_isPlaying.getValue())) {
                try {
                    long delay = (long) (1000 / _playbackSpeed.getValue());
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    break;
                }
                currentIndex++;
                updateUIState();
            }
            _isPlaying.postValue(false);
        });
        playbackThread.start();
    }

    public void nextStep() {
        if (explanation != null && currentIndex < explanation.steps.size() - 1) {
            currentIndex++;
            updateUIState();
        }
    }

    public void prevStep() {
        if (explanation != null && currentIndex > 0) {
            currentIndex--;
            updateUIState();
        }
    }
    
    public void toggleSpeed() {
        float current = _playbackSpeed.getValue() != null ? _playbackSpeed.getValue() : 1.0f;
        if (current == 1.0f) _playbackSpeed.setValue(2.0f);
        else if (current == 2.0f) _playbackSpeed.setValue(4.0f);
        else _playbackSpeed.setValue(1.0f);
    }

    private void updateUIState() {
        if (explanation != null && currentIndex >= 0 && currentIndex < explanation.steps.size()) {
            _stepCountMetrics.postValue("Step: " + (currentIndex + 1) + " / " + explanation.steps.size());
            _currentStep.postValue(explanation.steps.get(currentIndex));
        }
    }
    
    public String getGameName() { return explanation != null ? explanation.gameName : "Unknown"; }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (playbackThread != null) playbackThread.interrupt();
    }
}
