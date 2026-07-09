package com.example.healthfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ProgressFragment extends Fragment {

    private DailyLogViewModel viewModel;
    private LinearLayout llHistoryContainer;
    private TextView tvTotalDistance, tvTotalCalories, tvBmiScore;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        db = AppDatabase.getDatabase(getContext());
        llHistoryContainer = view.findViewById(R.id.llHistoryContainer);
        tvTotalDistance = view.findViewById(R.id.tvTotalDistance);
        tvTotalCalories = view.findViewById(R.id.tvTotalCalories);
        tvBmiScore = view.findViewById(R.id.tvBmiScore);

        view.findViewById(R.id.btnOpenMap).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WorkoutActivity.class);
            startActivity(intent);
        });

        viewModel = new ViewModelProvider(this).get(DailyLogViewModel.class);
        viewModel.getAllLogs().observe(getViewLifecycleOwner(), this::updateUI);

        loadUserData();

        return view;
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

        updateHistoryUI(logs);
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
