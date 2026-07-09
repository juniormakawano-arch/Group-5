package com.example.healthfit.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.healthfit.R;
import com.example.healthfit.data.AppDatabase;
import com.example.healthfit.data.DailyLog;
import com.example.healthfit.data.DailyLogViewModel;
import com.example.healthfit.data.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView tvHomeSteps, tvHomeDistance, tvHomeCalories, tvHomeWater, tvHomeActive, tvHomeSleep;
    private TextView tvFitnessTip;
    private EditText etHomeStepGoal;
    private ProgressBar pbHomeSteps, pbHomeWater, pbHomeActive;
    private DailyLogViewModel viewModel;
    private String currentDate, userEmail;
    private DailyLog currentLog;
    private User currentUser;

    private static final String[] FITNESS_TIPS = {
            "Stay hydrated! Drink at least 8 glasses of water today.",
            "Take the stairs instead of the elevator for a quick calorie burn.",
            "Consistency is key. Even a 15-minute walk makes a difference.",
            "Don't forget to stretch after your workout to improve flexibility.",
            "Try to get at least 7-8 hours of sleep for better recovery.",
            "Add some protein to your post-workout meal to help muscle repair.",
            "A short morning walk can boost your mood and energy for the day."
    };

    private static final double KM_PER_STEP = 0.00076;
    private static final double CALORIES_PER_STEP = 0.04;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvHomeSteps = view.findViewById(R.id.tvHomeSteps);
        tvHomeDistance = view.findViewById(R.id.tvHomeDistance);
        tvHomeCalories = view.findViewById(R.id.tvHomeCalories);
        tvHomeWater = view.findViewById(R.id.tvHomeWater);
        tvHomeActive = view.findViewById(R.id.tvHomeActive);
        tvHomeSleep = view.findViewById(R.id.tvHomeSleep);
        tvFitnessTip = view.findViewById(R.id.tvFitnessTip);
        etHomeStepGoal = view.findViewById(R.id.etHomeStepGoal);

        pbHomeSteps = view.findViewById(R.id.pbHomeSteps);
        pbHomeWater = view.findViewById(R.id.pbHomeWater);
        pbHomeActive = view.findViewById(R.id.pbHomeActive);

        SharedPreferences prefs = getActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", "");

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        viewModel = new ViewModelProvider(this).get(DailyLogViewModel.class);

        loadUserData();
        setRandomFitnessTip();

        viewModel.getLogByDate(currentDate, userEmail).observe(getViewLifecycleOwner(), log -> {
            if (log != null) {
                currentLog = log;
                updateUI(log);
            } else {
                currentLog = new DailyLog(currentDate, userEmail);
                viewModel.insert(currentLog);
                updateUI(currentLog);
            }
        });

        view.findViewById(R.id.btnQuickAddSteps).setOnClickListener(v -> {
            if (currentLog != null) {
                currentLog.steps += 500;
                currentLog.activeMinutes += 5;
                viewModel.update(currentLog);
            }
        });

        view.findViewById(R.id.btnUpdateHomeGoal).setOnClickListener(v -> updateGoalFromHome());

        return view;
    }

    private void setRandomFitnessTip() {
        int index = new java.util.Random().nextInt(FITNESS_TIPS.length);
        tvFitnessTip.setText(FITNESS_TIPS[index]);
    }

    private void updateGoalFromHome() {
        String goalStr = etHomeStepGoal.getText().toString();
        if (goalStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a goal", Toast.LENGTH_SHORT).show();
            return;
        }

        int newGoal = Integer.parseInt(goalStr);
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = AppDatabase.getDatabase(getContext()).userDao().getUserByEmail(userEmail);
            if (user != null) {
                user.stepGoal = newGoal;
                AppDatabase.getDatabase(getContext()).userDao().update(user);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        currentUser = user;
                        if (currentLog != null) updateUI(currentLog);
                        Toast.makeText(getContext(), "Step goal updated!", Toast.LENGTH_SHORT).show();
                        etHomeStepGoal.setText("");
                        etHomeStepGoal.clearFocus();
                    });
                }
            }
        });
    }

    private void loadUserData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = AppDatabase.getDatabase(getContext()).userDao().getUserByEmail(userEmail);
            if (user != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    currentUser = user;
                    if (currentLog != null) updateUI(currentLog);
                });
            }
        });
    }

    private void updateUI(DailyLog log) {
        int steps = log.steps;
        tvHomeSteps.setText(String.format(Locale.getDefault(), "%, d", steps));
        double distance = steps * KM_PER_STEP;
        tvHomeDistance.setText(String.format(Locale.getDefault(), "%.1f", distance));
        double calories = steps * CALORIES_PER_STEP;
        tvHomeCalories.setText(String.format(Locale.getDefault(), "%.0f", calories));

        tvHomeWater.setText(String.valueOf(log.water));
        tvHomeActive.setText(String.valueOf(log.activeMinutes));
        tvHomeSleep.setText(String.format(Locale.getDefault(), "%dh", log.sleepHours));

        if (currentUser != null) {
            pbHomeSteps.setMax(currentUser.stepGoal);
            pbHomeSteps.setProgress(Math.min(steps, currentUser.stepGoal));

            pbHomeWater.setMax(currentUser.waterGoal);
            pbHomeWater.setProgress(Math.min(log.water, currentUser.waterGoal));

            pbHomeActive.setMax(currentUser.activeMinGoal);
            pbHomeActive.setProgress(Math.min(log.activeMinutes, currentUser.activeMinGoal));
        }
    }
}
