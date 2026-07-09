package com.example.healthfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.healthfit.R;
import com.example.healthfit.data.DailyLogViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvHomeSteps, tvHomeDistance, tvHomeCalories;
    private DailyLogViewModel viewModel;
    private String currentDate;

    private static final double KM_PER_STEP = 0.00076;
    private static final double CALORIES_PER_STEP = 0.04;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvHomeSteps = view.findViewById(R.id.tvHomeSteps);
        tvHomeDistance = view.findViewById(R.id.tvHomeDistance);
        tvHomeCalories = view.findViewById(R.id.tvHomeCalories);

        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        viewModel = new ViewModelProvider(this).get(DailyLogViewModel.class);

        viewModel.getLogByDate(currentDate).observe(getViewLifecycleOwner(), log -> {
            if (log != null) {
                updateUI(log.steps);
            } else {
                updateUI(0);
            }
        });

        return view;
    }

    private void updateUI(int steps) {
        tvHomeSteps.setText(String.format(Locale.getDefault(), "%, d", steps));
        double distance = steps * KM_PER_STEP;
        tvHomeDistance.setText(String.format(Locale.getDefault(), "%.1f", distance));
        double calories = steps * CALORIES_PER_STEP;
        tvHomeCalories.setText(String.format(Locale.getDefault(), "%.0f", calories));
    }
}
