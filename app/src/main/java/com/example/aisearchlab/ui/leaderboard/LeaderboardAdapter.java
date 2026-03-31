package com.example.aisearchlab.ui.leaderboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aisearchlab.R;
import com.example.aisearchlab.models.ScoreEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ScoreViewHolder> {

    private List<ScoreEntity> scores = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        ScoreEntity score = scores.get(position);
        
        holder.tvGameName.setText(score.gameName);
        holder.tvAlgorithmName.setText(score.algorithmName);
        holder.tvNodes.setText("Nodes Explored: " + score.nodesExplored);
        holder.tvCost.setText("Cost: " + (int)score.pathCost);
        
        Date date = new Date(score.timestamp);
        holder.tvDate.setText(dateFormat.format(date));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public void setScores(List<ScoreEntity> newScores) {
        this.scores = newScores;
        notifyDataSetChanged();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView tvGameName, tvAlgorithmName, tvNodes, tvCost, tvDate;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGameName = itemView.findViewById(R.id.tv_game_name);
            tvAlgorithmName = itemView.findViewById(R.id.tv_algorithm_name);
            tvNodes = itemView.findViewById(R.id.tv_nodes);
            tvCost = itemView.findViewById(R.id.tv_cost);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
