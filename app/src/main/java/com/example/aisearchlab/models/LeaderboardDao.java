package com.example.aisearchlab.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LeaderboardDao {

    @Insert
    void insertScore(ScoreEntity score);

    @Query("SELECT * FROM leaderboard_scores WHERE gameName = :gameName ORDER BY pathCost ASC, nodesExplored ASC LIMIT 10")
    LiveData<List<ScoreEntity>> getTopScoresForGame(String gameName);
    
    @Query("SELECT * FROM leaderboard_scores ORDER BY timestamp DESC LIMIT 50")
    LiveData<List<ScoreEntity>> getRecentScores();
    
    @Query("DELETE FROM leaderboard_scores")
    void deleteAllScores();
}
