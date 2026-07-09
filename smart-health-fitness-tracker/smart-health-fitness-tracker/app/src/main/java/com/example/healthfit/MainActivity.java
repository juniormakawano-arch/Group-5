package com.example.healthfit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.healthfit.fragments.ActivityFragment;
import com.example.healthfit.fragments.ComingSoonFragment;
import com.example.healthfit.fragments.HomeFragment;
import com.example.healthfit.fragments.ProfileFragment;
import com.example.healthfit.fragments.ProgressFragment;
import com.example.healthfit.fragments.WorkoutFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.navigation_activity) {
                selectedFragment = new ActivityFragment();
            } else if (id == R.id.navigation_workout) {
                selectedFragment = new WorkoutFragment();
            } else if (id == R.id.navigation_progress) {
                selectedFragment = new ProgressFragment();
            } else if (id == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Set default selection
        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.navigation_home);
        }
    }
}
