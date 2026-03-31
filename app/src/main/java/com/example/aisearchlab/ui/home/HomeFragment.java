package com.example.aisearchlab.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.aisearchlab.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        View card8Puzzle = view.findViewById(R.id.card_8_puzzle);
        View cardWaterJug = view.findViewById(R.id.card_water_jug);
        View cardNQueens = view.findViewById(R.id.card_n_queens);

        Animation slideInUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_up);
        
        // Apply staggered entrance animations
        card8Puzzle.startAnimation(slideInUp);
        
        Animation slideInUp2 = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_up);
        slideInUp2.setStartOffset(100);
        cardWaterJug.startAnimation(slideInUp2);
        
        Animation slideInUp3 = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_up);
        slideInUp3.setStartOffset(200);
        cardNQueens.startAnimation(slideInUp3);
        
        // Touch feedback
        setupTouchFeedback(card8Puzzle);
        setupTouchFeedback(cardWaterJug);
        setupTouchFeedback(cardNQueens);

        card8Puzzle.setOnClickListener(v -> 
                Navigation.findNavController(v).navigate(R.id.action_home_to_eightPuzzle)
        );
        
        cardWaterJug.setOnClickListener(v -> 
                Navigation.findNavController(v).navigate(R.id.action_home_to_waterJug)
        );

        cardNQueens.setOnClickListener(v -> 
                Navigation.findNavController(v).navigate(R.id.action_home_to_nQueens)
        );
        
        return view;
    }
    
    private void setupTouchFeedback(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(300).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                    break;
            }
            return false;
        });
    }
}
