package com.example.aisearchlab.ui.visualizer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aisearchlab.models.AlgorithmExplanation;

public class SharedExplanationViewModel extends ViewModel {
    private final MutableLiveData<AlgorithmExplanation> _explanation = new MutableLiveData<>();
    public LiveData<AlgorithmExplanation> explanation = _explanation;

    public void setExplanation(AlgorithmExplanation exp) {
        _explanation.setValue(exp);
    }
}
