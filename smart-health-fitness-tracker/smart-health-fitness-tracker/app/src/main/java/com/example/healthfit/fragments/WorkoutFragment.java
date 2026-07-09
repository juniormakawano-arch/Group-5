package com.example.healthfit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthfit.R;
import com.example.healthfit.WorkoutActivity;

public class WorkoutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        // Map Button
        view.findViewById(R.id.btnStartCardio).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WorkoutActivity.class);
            startActivity(intent);
        });

        // Strength Tips Toggle
        LinearLayout llStrengthTips = view.findViewById(R.id.llStrengthTips);
        view.findViewById(R.id.btnStartStrength).setOnClickListener(v -> {
            if (llStrengthTips.getVisibility() == View.GONE) {
                llStrengthTips.setVisibility(View.VISIBLE);
            } else {
                llStrengthTips.setVisibility(View.GONE);
            }
        });

        // Yoga Tips Toggle
        LinearLayout llYogaTips = view.findViewById(R.id.llYogaTips);
        view.findViewById(R.id.btnStartYoga).setOnClickListener(v -> {
            if (llYogaTips.getVisibility() == View.GONE) {
                llYogaTips.setVisibility(View.VISIBLE);
            } else {
                llYogaTips.setVisibility(View.GONE);
            }
        });

        return view;
    }
}
