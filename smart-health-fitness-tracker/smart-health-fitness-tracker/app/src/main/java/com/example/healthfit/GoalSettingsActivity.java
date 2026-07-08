package com.example.healthfit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GoalSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_settings);

        EditText etSteps = findViewById(R.id.etStepGoal);
        EditText etWater = findViewById(R.id.etWaterGoal);
        Button btnSave = findViewById(R.id.btnSaveGoals);

        // Load existing goals from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("HealthPrefs", MODE_PRIVATE);
        etSteps.setText(String.valueOf(prefs.getInt("step_goal", 5000)));
        etWater.setText(String.valueOf(prefs.getInt("water_goal", 2000)));

        btnSave.setOnClickListener(v -> {
            try {
                int stepGoal = Integer.parseInt(etSteps.getText().toString());
                int waterGoal = Integer.parseInt(etWater.getText().toString());

                // Save goals so Member 5 (Dashboard) can use them
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("step_goal", stepGoal);
                editor.putInt("water_goal", waterGoal);
                editor.apply();

                Toast.makeText(this, "Goals updated!", Toast.LENGTH_SHORT).show();
                finish();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });
    }
}