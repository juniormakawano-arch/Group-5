package com.example.healthfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

/**
 * Main dashboard: tracks steps, water intake, and estimated calories.
 * Updated to include a Navigation Drawer (Hamburger Menu).
 */
public class DashboardActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "health_tracker_prefs";
    private static final double CALORIES_PER_STEP = 0.04;

    private TextView tvWelcome, tvSteps, tvWater, tvCalories;
    private Button btnAddSteps, btnAddWater, btnViewLog;

    // Hamburger Menu components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private SharedPreferences prefs;
    private int steps;
    private int waterGlasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 1. Setup the Toolbar (This holds the Hamburger icon)
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. Initialize Drawer and Navigation View
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // 3. Setup Hamburger Toggle logic
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 4. Handle Menu Item Clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_workouts) {
                // Member 4's Workout Page
                startActivity(new Intent(this, WorkoutListActivity.class));
            } else if (id == R.id.nav_goals) {
                // Member 4's Goals Page
                startActivity(new Intent(this, GoalSettingsActivity.class));
            } else if (id == R.id.nav_logout) {
                finish(); // Close dashboard and return to Login
            }

            drawerLayout.closeDrawers(); // Close the menu when an item is clicked
            return true;
        });

        // --- Rest of your original logic ---
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