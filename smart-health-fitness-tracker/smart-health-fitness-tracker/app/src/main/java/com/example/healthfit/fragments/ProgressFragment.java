package com.example.healthfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.healthfit.R;
import com.example.healthfit.WorkoutActivity;
import com.example.healthfit.data.AppDatabase;
import com.example.healthfit.data.DailyLog;
import com.example.healthfit.data.DailyLogViewModel;
import com.example.healthfit.data.User;
import com.example.healthfit.data.Achievement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ProgressFragment extends Fragment {

    private DailyLogViewModel viewModel;
    private LinearLayout llHistoryContainer, llBadgeContainer;
    private TextView tvTotalDistance, tvTotalCalories, tvBmiScore;
    private EditText etTodayWeight;
    private AppDatabase db;
    private String currentDate;
    private DailyLog currentLog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        db = AppDatabase.getDatabase(getContext());
        llHistoryContainer = view.findViewById(R.id.llHistoryContainer);
        llBadgeContainer = view.findViewById(R.id.llBadgeContainer);
        tvTotalDistance = view.findViewById(R.id.tvTotalDistance);
        tvTotalCalories = view.findViewById(R.id.tvTotalCalories);
        tvBmiScore = view.findViewById(R.id.tvBmiScore);
        etTodayWeight = view.findViewById(R.id.etTodayWeight);

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        view.findViewById(R.id.btnOpenMap).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WorkoutActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btnSaveWeight).setOnClickListener(v -> saveTodayWeight());

        viewModel = new ViewModelProvider(this).get(DailyLogViewModel.class);
        viewModel.getAllLogs().observe(getViewLifecycleOwner(), this::updateUI);

        viewModel.getLogByDate(currentDate).observe(getViewLifecycleOwner(), log -> {
            currentLog = log;
        });

        db.achievementDao().getAllAchievements().observe(getViewLifecycleOwner(), this::updateBadges);

        loadUserData();

        return view;
    }

    private void saveTodayWeight() {
        String weightStr = etTodayWeight.getText().toString();
        if (weightStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter weight", Toast.LENGTH_SHORT).show();
            return;
        }

        float weight = Float.parseFloat(weightStr);
        Executors.newSingleThreadExecutor().execute(() -> {
            if (currentLog == null) {
                currentLog = new DailyLog(currentDate);
                currentLog.weight = weight;
                db.logDao().insert(currentLog);
            } else {
                currentLog.weight = weight;
                db.logDao().update(currentLog);
            }
            
            // Also update the User profile with latest weight
            SharedPreferences prefs = getActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
            String email = prefs.getString("user_email", "");
            User user = db.userDao().getUserByEmail(email);
            if (user != null) {
                user.weight = weight;
                db.userDao().update(user);
            }

            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Weight Logged!", Toast.LENGTH_SHORT).show();
                etTodayWeight.setText("");
                etTodayWeight.clearFocus();
                loadUserData(); // Refresh BMI
            });
        });
    }

    private void updateBadges(List<Achievement> achievements) {
        if (achievements == null) return;
        llBadgeContainer.removeAllViews();
        for (Achievement achievement : achievements) {
            ImageView badge = new ImageView(getContext());
            int size = (int) (60 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(8, 8, 8, 8);
            badge.setLayoutParams(params);
            badge.setImageResource(android.R.drawable.btn_star_big_on);
            badge.setAlpha(achievement.isUnlocked ? 1.0f : 0.2f);
            
            badge.setOnClickListener(v -> Toast.makeText(getContext(), 
                achievement.title + ": " + achievement.description, Toast.LENGTH_SHORT).show());
            
            llBadgeContainer.addView(badge);
        }
    }

    private void loadUserData() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        String email = prefs.getString("user_email", "");

        Executors.newSingleThreadExecutor().execute(() -> {
            User user = db.userDao().getUserByEmail(email);
            if (user != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> calculateBMI(user));
            }
        });
    }

    private void calculateBMI(User user) {
        if (user.height > 0 && user.weight > 0) {
            float heightInMeters = user.height / 100;
            float bmi = user.weight / (heightInMeters * heightInMeters);
            String status = getBmiStatus(bmi);
            tvBmiScore.setText(String.format(Locale.getDefault(), "%.1f (%s)", bmi, status));
        } else {
            tvBmiScore.setText("Profile incomplete");
        }
    }

    private String getBmiStatus(float bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25) return "Normal";
        if (bmi < 30) return "Overweight";
        return "Obese";
    }

    private void updateUI(List<DailyLog> logs) {
        if (logs == null || logs.isEmpty()) return;

        double totalDist = 0;
        int totalCal = 0;

        for (DailyLog log : logs) {
            totalDist += (log.steps * 0.00076);
            totalCal += (log.steps * 0.04);
        }

        tvTotalDistance.setText(String.format(Locale.getDefault(), "%.1f km", totalDist));
        tvTotalCalories.setText(String.format(Locale.getDefault(), "%, d", totalCal));

        updateChart(logs);
        updateHistoryUI(logs);
    }

    private void updateChart(List<DailyLog> logs) {
        if (getView() == null) return;
        View[] bars = {
                getView().findViewById(R.id.chartBar1),
                getView().findViewById(R.id.chartBar2),
                getView().findViewById(R.id.chartBar3),
                getView().findViewById(R.id.chartBar4)
        };

        int maxSteps = 0;
        for (DailyLog log : logs) if (log.steps > maxSteps) maxSteps = log.steps;
        if (maxSteps == 0) maxSteps = 10000;

        int logSize = logs.size();
        for (int i = 0; i < 4; i++) {
            if (i < logSize) {
                DailyLog log = logs.get(logSize - 1 - i);
                float ratio = (float) log.steps / maxSteps;
                int height = (int) (ratio * 150 * getResources().getDisplayMetrics().density);
                ViewGroup.LayoutParams params = bars[i].getLayoutParams();
                params.height = Math.max(height, 10);
                bars[i].setLayoutParams(params);
            }
        }
    }

    private void updateHistoryUI(List<DailyLog> logs) {
        llHistoryContainer.removeAllViews();
        int count = 0;
        for (int i = logs.size() - 1; i >= 0 && count < 5; i--) {
            DailyLog log = logs.get(i);
            View itemView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, llHistoryContainer, false);
            TextView tvDate = itemView.findViewById(android.R.id.text1);
            TextView tvStats = itemView.findViewById(android.R.id.text2);

            tvDate.setText(log.date);
            tvStats.setText(String.format(Locale.getDefault(), "%d steps • %d active min", log.steps, log.activeMinutes));
            
            llHistoryContainer.addView(itemView);
            count++;
        }
    }
}
