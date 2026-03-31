package com.example.aisearchlab.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "leaderboard_scores")
public class ScoreEntity {
    
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    @NonNull
    public String gameName;
    
    @NonNull
    public String algorithmName;
    
    public int nodesExplored;
    public double pathCost;
    public long timestamp;

    public ScoreEntity(@NonNull String gameName, @NonNull String algorithmName, int nodesExplored, double pathCost, long timestamp) {
        this.gameName = gameName;
        this.algorithmName = algorithmName;
        this.nodesExplored = nodesExplored;
        this.pathCost = pathCost;
        this.timestamp = timestamp;
    }
}
