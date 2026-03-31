package com.example.aisearchlab.ui.leaderboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.aisearchlab.models.ScoreEntity;
import com.example.aisearchlab.repository.LeaderboardRepository;

import java.util.List;

public class LeaderboardViewModel extends AndroidViewModel {

    private final LeaderboardRepository repository;
    private final LiveData<List<ScoreEntity>> recentScores;

    public LeaderboardViewModel(@NonNull Application application) {
        super(application);
        repository = new LeaderboardRepository(application);
        recentScores = repository.getRecentScores();
    }

    public LiveData<List<ScoreEntity>> getRecentScores() {
        return recentScores;
    }

    public void insertScore(ScoreEntity score) {
        repository.insertScore(score);
    }
}
