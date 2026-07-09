package com.example.healthfit.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.healthfit.R;
import com.example.healthfit.data.DailyLog;
import com.example.healthfit.data.DailyLogViewModel;
import com.example.healthfit.data.AppDatabase;
import com.example.healthfit.data.Meal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ActivityFragment extends Fragment {

    private DailyLogViewModel viewModel;
    private String currentDate;
    private TextView tvWaterStats, tvStreak, tvCaloriesTotal;
    private ProgressBar pbQuest;
    private DailyLog currentLog;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        db = AppDatabase.getDatabase(getContext());
        tvWaterStats = view.findViewById(R.id.tvWaterStats);
        tvStreak = view.findViewById(R.id.tvStreak);
        tvCaloriesTotal = view.findViewById(R.id.tvCaloriesTotal);
        pbQuest = view.findViewById(R.id.pbQuest);
        
        Button btnAddWater = view.findViewById(R.id.btnAddWaterActivity);
        Button btnAddMeal = view.findViewById(R.id.btnAddMeal);
        Button btnBreathing = view.findViewById(R.id.btnStartBreathing);
        View fab = view.findViewById(R.id.fabQuickAction);

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        viewModel = new ViewModelProvider(this).get(DailyLogViewModel.class);

        viewModel.getLogByDate(currentDate).observe(getViewLifecycleOwner(), log -> {
            if (log != null) {
                currentLog = log;
                updateUI();
            }
        });

        db.mealDao().getTotalCaloriesByDate(currentDate).observe(getViewLifecycleOwner(), total -> {
            int cal = total != null ? total : 0;
            tvCaloriesTotal.setText(cal + " / 2,500 kcal");
        });

        btnAddWater.setOnClickListener(v -> {
            if (currentLog != null) {
                currentLog.water += 1;
                viewModel.update(currentLog);
            }
        });

        btnAddMeal.setOnClickListener(v -> logDummyMeal());
        fab.setOnClickListener(v -> Toast.makeText(getContext(), "Quick Action Clicked!", Toast.LENGTH_SHORT).show());
        
        btnBreathing.setOnClickListener(v -> startBreathingExercise(btnBreathing));

        return view;
    }

    private void logDummyMeal() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Meal meal = new Meal(currentDate, "Snack", "Apple", 95);
            db.mealDao().insert(meal);
        });
        Toast.makeText(getContext(), "Logged Apple (95 kcal)", Toast.LENGTH_SHORT).show();
    }

    private void startBreathingExercise(Button btn) {
        btn.setEnabled(false);
        btn.setText("Inhale...");
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            btn.setText("Exhale...");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                btn.setText("Done!");
                btn.setEnabled(true);
            }, 3000);
        }, 3000);
    }

    private void updateUI() {
        if (currentLog != null) {
            tvWaterStats.setText(currentLog.water + "/8 Glasses");
            int progress = (int) ((currentLog.steps / 6500.0) * 100);
            pbQuest.setProgress(Math.min(progress, 100));
            tvStreak.setText("🔥 5 Day Streak"); // Placeholder logic
        }
    }
}
