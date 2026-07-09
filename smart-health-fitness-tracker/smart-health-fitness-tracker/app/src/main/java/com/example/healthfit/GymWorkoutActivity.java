package com.example.healthfit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GymWorkoutActivity extends AppCompatActivity {

    private EditText etExerciseName, etSets, etReps, etWeight;
    private Button btnSaveGymWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_workout);

        etExerciseName = findViewById(R.id.etExerciseName);
        etSets = findViewById(R.id.etSets);
        etReps = findViewById(R.id.etReps);
        etWeight = findViewById(R.id.etWeight);
        btnSaveGymWorkout = findViewById(R.id.btnSaveGymWorkout);

        btnSaveGymWorkout.setOnClickListener(v -> saveWorkout());
    }

    private void saveWorkout() {
        String name = etExerciseName.getText().toString().trim();
        String sets = etSets.getText().toString().trim();
        String reps = etReps.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();

        if (name.isEmpty() || sets.isEmpty() || reps.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // For now, just show a toast. In a real app, this would save to a database.
        String message = "Logged: " + name + " - " + sets + " sets x " + reps + " reps at " + weight + "kg";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
        // Clear fields or finish
        finish();
    }
}
