package com.example.aisearchlab;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aisearchlab.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup bottom navigation here later
    }
}
