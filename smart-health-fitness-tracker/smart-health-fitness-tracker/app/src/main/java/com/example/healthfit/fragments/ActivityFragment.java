package com.example.healthfit.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.healthfit.R;
import com.example.healthfit.MealLoggingActivity;
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
    private String currentDate, userEmail;
    private TextView tvWaterStats, tvStreak, tvCaloriesTotal, tvBreathingAction;
    private ProgressBar pbQuest;
    private FrameLayout flBreathingContainer;
    private View viewBreathingCircle;
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
        
        flBreathingContainer = view.findViewById(R.id.flBreathingContainer);
        viewBreathingCircle = view.findViewById(R.id.viewBreathingCircle);
        tvBreathingAction = view.findViewById(R.id.tvBreathingAction);
        
        Button btnAddWater = view.findViewById(R.id.btnAddWaterActivity);
        Button btnAddMeal = view.findViewById(R.id.btnAddMeal);
        Button btnBreathing = view.findViewById(R.id.btnStartBreathing);
        View fab = view.findViewById(R.id.fabQuickAction);

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        viewModel = new ViewModelProvider(this).get(DailyLogViewModel.class);

        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("login_prefs", android.content.Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", "");

        viewModel.getLogByDate(currentDate, userEmail).observe(getViewLifecycleOwner(), log -> {
            if (log != null) {
                currentLog = log;
                updateUI();
            }
        });

        db.mealDao().getTotalCaloriesByDate(currentDate, userEmail).observe(getViewLifecycleOwner(), total -> {
            int cal = total != null ? total : 0;
            tvCaloriesTotal.setText(cal + " / 2,500 kcal");
        });

        btnAddWater.setOnClickListener(v -> {
            if (currentLog != null) {
                currentLog.water += 1;
                viewModel.update(currentLog);
            }
        });

        btnAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MealLoggingActivity.class);
            startActivity(intent);
        });

        fab.setOnClickListener(v -> Toast.makeText(getContext(), "Quick Action Clicked!", Toast.LENGTH_SHORT).show());
        
        btnBreathing.setOnClickListener(v -> startBreathingExercise(btnBreathing));

        return view;
    }

    private void startBreathingExercise(Button btn) {
        btn.setEnabled(false);
        flBreathingContainer.setVisibility(View.VISIBLE);
        
        // Create pulsing animation
        ObjectAnimator scaleAnim = ObjectAnimator.ofPropertyValuesHolder(
                viewBreathingCircle,
                PropertyValuesHolder.ofFloat("scaleX", 1f, 2.5f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 2.5f)
        );
        scaleAnim.setDuration(4000);
        scaleAnim.setRepeatCount(2); // 3 breaths total (0, 1, 2)
        scaleAnim.setRepeatMode(ObjectAnimator.REVERSE);
        
        scaleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                tvBreathingAction.setText("Inhale...");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (tvBreathingAction.getText().equals("Inhale...")) {
                    tvBreathingAction.setText("Exhale...");
                } else {
                    tvBreathingAction.setText("Inhale...");
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tvBreathingAction.setText("Relaxed");
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    flBreathingContainer.setVisibility(View.GONE);
                    btn.setEnabled(true);
                    btn.setText("Start");
                    if (currentLog != null) {
                        currentLog.activeMinutes += 1;
                        viewModel.update(currentLog);
                        Toast.makeText(getContext(), "+1 Active Minute for Mindfulness", Toast.LENGTH_SHORT).show();
                    }
                }, 2000);
            }
        });
        
        scaleAnim.start();
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
