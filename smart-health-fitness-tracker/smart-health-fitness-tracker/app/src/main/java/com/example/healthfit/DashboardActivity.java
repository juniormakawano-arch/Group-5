package com.example.healthfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.healthfit.data.DailyLog;
import com.example.healthfit.data.DailyLogViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity implements SensorEventListener {

    private static final String PREFS_NAME = "health_tracker_prefs";
    private static final double CALORIES_PER_STEP = 0.04;
    private static final double KM_PER_STEP = 0.00076;

    private TextView tvWelcome, tvSteps, tvWater, tvDistance, tvActiveMinutes, tvFloors, tvSleep, tvReadinessScore, tvCoachingTip, tvChallengeName, tvCaloriesIn;
    private ProgressBar pbReadiness, pbChallenge;
    private Button btnAddSteps, btnAddWater, btnViewLog, btnLogout, btnStartWorkout;

    private SharedPreferences prefs;
    private DailyLogViewModel viewModel;
    private String currentDate, userEmail;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorPresent = false;
    private int initialStepCount = -1;

    private int steps;
    private int waterGlasses;
    private int floors;
    private int activeMinutes;
    private int sleepHours;
    private int sleepMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences loginPrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        userEmail = loginPrefs.getString("user_email", "");

        // Initialize Views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvSteps = findViewById(R.id.tvSteps);
        tvDistance = findViewById(R.id.tvDistance);
        tvActiveMinutes = findViewById(R.id.tvActiveMinutes);
        tvFloors = findViewById(R.id.tvFloors);
        tvSleep = findViewById(R.id.tvSleep);
        tvReadinessScore = findViewById(R.id.tvReadinessScore);
        tvCoachingTip = findViewById(R.id.tvCoachingTip);
        pbReadiness = findViewById(R.id.pbReadiness);
        tvChallengeName = findViewById(R.id.tvChallengeName);
        pbChallenge = findViewById(R.id.pbChallenge);
        tvCaloriesIn = findViewById(R.id.tvCaloriesIn);

        btnAddSteps = findViewById(R.id.btnAddSteps);
        btnAddWater = findViewById(R.id.btnAddWater);
        btnViewLog = findViewById(R.id.btnViewLog);
        btnLogout = findViewById(R.id.btnLogout);
        btnStartWorkout = findViewById(R.id.btnStartWorkout);

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        viewModel = new ViewModelProvider(this).get(DailyLogViewModel.class);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        }

        viewModel.getLogByDate(currentDate, userEmail).observe(this, log -> {
            if (log != null) {
                steps = log.steps;
                waterGlasses = log.water;
                floors = log.floors;
                activeMinutes = log.activeMinutes;
                sleepHours = log.sleepHours;
                sleepMinutes = log.sleepMinutes;
                updateUI();
            } else {
                DailyLog newLog = new DailyLog(currentDate, userEmail);
                // Set some defaults
                newLog.sleepHours = 7;
                viewModel.insert(newLog);
            }
        });

        findViewById(R.id.tvSleep).setOnClickListener(v -> {
            Intent intent = new Intent(this, SleepActivity.class);
            startActivity(intent);
        });

        String username = getIntent().getStringExtra("username");
        if (username != null) {
            tvWelcome.setText("Welcome, " + username + "!");
        }

        loadData();
        updateUI();

        btnAddSteps.setOnClickListener(v -> {
            steps += 500;
            activeMinutes += 5;
            if (steps % 1000 == 0) floors += 1;
            saveToDatabase();
        });

        btnAddWater.setOnClickListener(v -> {
            waterGlasses += 1;
            saveToDatabase();
        });

        btnAddSteps.post(() -> {
            Intent serviceIntent = new Intent(this, StepCounterService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        });

        btnStartWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutActivity.class);
            startActivity(intent);
        });

        btnViewLog.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LogActivity.class);
            intent.putExtra("steps", steps);
            intent.putExtra("water", waterGlasses);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorPresent) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorPresent) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalStepsSinceBoot = (int) event.values[0];
            if (initialStepCount == -1) {
                initialStepCount = totalStepsSinceBoot;
            }
            // For simplicity, we add the difference to our current steps if we want real-time updates
            // But usually, you'd track the delta since last app open.
            // In a real app, you'd store the 'boot step count' to calculate daily steps.
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void saveToDatabase() {
        viewModel.getLogByDate(currentDate, userEmail).observe(this, log -> {
            if (log != null) {
                log.steps = steps;
                log.water = waterGlasses;
                log.floors = floors;
                log.activeMinutes = activeMinutes;
                viewModel.update(log);
            }
        });
    }

    private void logout() {
        SharedPreferences loginPrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        loginPrefs.edit().clear().apply();
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadData() {
        steps = prefs.getInt("steps", 0);
        waterGlasses = prefs.getInt("water", 0);
        floors = prefs.getInt("floors", 0);
        activeMinutes = prefs.getInt("activeMinutes", 0);
        sleepHours = prefs.getInt("sleep_hours", 7);
        sleepMinutes = prefs.getInt("sleep_minutes", 0);
    }

    private void saveData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("steps", steps);
        editor.putInt("water", waterGlasses);
        editor.putInt("floors", floors);
        editor.putInt("activeMinutes", activeMinutes);
        editor.apply();
    }

    private void updateUI() {
        tvSteps.setText(String.valueOf(steps));
        tvFloors.setText(String.valueOf(floors));
        tvActiveMinutes.setText(String.valueOf(activeMinutes));
        tvSleep.setText(String.format(Locale.getDefault(), "%dh %dm", sleepHours, sleepMinutes));
        if (tvCaloriesIn != null) tvCaloriesIn.setText(String.valueOf(0)); // Default for now

        pbChallenge.setProgress(steps);
        if (steps >= 10000) {
            tvChallengeName.setText("Challenge Completed! 🎉");
        }

        double distance = steps * KM_PER_STEP;
        tvDistance.setText(String.format(Locale.getDefault(), "%.2f", distance));

        // Calculate Readiness Score (Simple logic: based on steps and sleep)
        int sleepScore = Math.min(100, (sleepHours * 10 + sleepMinutes / 6));
        int activityScore = Math.min(100, (steps / 100));
        int readiness = (sleepScore + activityScore) / 2;

        tvReadinessScore.setText(String.valueOf(readiness));
        pbReadiness.setProgress(readiness);

        updateCoachingTip(readiness);
    }

    private void updateCoachingTip(int readiness) {
        if (readiness > 80) {
            tvCoachingTip.setText("Your body is fully recovered. Great day for a run!");
        } else if (readiness > 50) {
            tvCoachingTip.setText("You're doing well. Keep up the consistent movement.");
        } else {
            tvCoachingTip.setText("Take it easy today. Focus on light walking and recovery.");
        }
    }
}
