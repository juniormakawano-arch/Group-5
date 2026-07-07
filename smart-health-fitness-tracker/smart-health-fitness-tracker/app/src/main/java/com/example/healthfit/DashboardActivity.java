package com.example.healthfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Main dashboard: tracks steps, water intake, and estimated calories.
 * Data is persisted locally with SharedPreferences for this starter version.
 *
 * TODO for team:
 *  - Replace simulated step button with real Android Sensor (TYPE_STEP_COUNTER)
 *  - Replace SharedPreferences with SQLite/Room or Firebase for multi-day history
 *  - Add user profile (weight/height) to make calorie estimate accurate
 */
public class DashboardActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "health_tracker_prefs";
    private static final double CALORIES_PER_STEP = 0.04; // rough average

    private TextView tvWelcome, tvSteps, tvWater, tvCalories;
    private Button btnAddSteps, btnAddWater, btnViewLog;

    private SharedPreferences prefs;
    private int steps;
    private int waterGlasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvSteps = findViewById(R.id.tvSteps);
        tvWater = findViewById(R.id.tvWater);
        tvCalories = findViewById(R.id.tvCalories);
        btnAddSteps = findViewById(R.id.btnAddSteps);
        btnAddWater = findViewById(R.id.btnAddWater);
        btnViewLog = findViewById(R.id.btnViewLog);

        String username = getIntent().getStringExtra("username");
        if (username != null) {
            tvWelcome.setText("Welcome, " + username + "!");
        }

        loadData();
        updateUI();

        btnAddSteps.setOnClickListener(v -> {
            steps += 500;
            saveData();
            updateUI();
        });

        btnAddWater.setOnClickListener(v -> {
            waterGlasses += 1;
            saveData();
            updateUI();
        });

        btnViewLog.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LogActivity.class);
            intent.putExtra("steps", steps);
            intent.putExtra("water", waterGlasses);
            startActivity(intent);
        });
    }

    private void loadData() {
        steps = prefs.getInt("steps", 0);
        waterGlasses = prefs.getInt("water", 0);
    }

    private void saveData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("steps", steps);
        editor.putInt("water", waterGlasses);
        editor.apply();
    }

    private void updateUI() {
        tvSteps.setText(String.valueOf(steps));
        tvWater.setText(String.valueOf(waterGlasses));
        int calories = (int) (steps * CALORIES_PER_STEP);
        tvCalories.setText(calories + " kcal");
    }
}
