package com.example.aisearchlab.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.aisearchlab.models.AppDatabase;
import com.example.aisearchlab.models.LeaderboardDao;
import com.example.aisearchlab.models.ScoreEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LeaderboardRepository {

    private LeaderboardDao leaderboardDao;
    private final ExecutorService executorService;

    public LeaderboardRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        leaderboardDao = db.leaderboardDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<List<ScoreEntity>> getTopScoresForGame(String gameName) {
        return leaderboardDao.getTopScoresForGame(gameName);
    }

    public LiveData<List<ScoreEntity>> getRecentScores() {
        return leaderboardDao.getRecentScores();
    }

    public void insertScore(ScoreEntity score) {
        executorService.execute(() -> {
            leaderboardDao.insertScore(score);
        });
    }
}
