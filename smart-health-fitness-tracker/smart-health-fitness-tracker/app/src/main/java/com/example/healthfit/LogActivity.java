package com.example.healthfit;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Placeholder log/history screen.
 * TODO for team: pull from a real database to show multi-day history,
 * and maybe add a chart (MPAndroidChart library works well here).
 */
public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        TextView tvLogSteps = findViewById(R.id.tvLogSteps);
        TextView tvLogWater = findViewById(R.id.tvLogWater);

        int steps = getIntent().getIntExtra("steps", 0);
        int water = getIntent().getIntExtra("water", 0);

        tvLogSteps.setText("Steps: " + steps);
        tvLogWater.setText("Water: " + water + " glasses");
    }
}
