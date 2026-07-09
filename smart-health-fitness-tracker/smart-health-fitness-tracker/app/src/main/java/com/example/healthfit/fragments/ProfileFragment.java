package com.example.healthfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.healthfit.LoginActivity;
import com.example.healthfit.R;
import com.example.healthfit.data.AppDatabase;
import com.example.healthfit.data.User;
import com.example.healthfit.data.UserDao;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private EditText etHeight, etWeight, etStepGoal, etWaterGoal, etActiveGoal;
    private EditText etCurrentPassword, etNewPassword;
    private TextView tvName, tvEmail;
    private LinearLayout llChangePassword;
    private UserDao userDao;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userDao = AppDatabase.getDatabase(getContext()).userDao();
        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        etHeight = view.findViewById(R.id.etHeight);
        etWeight = view.findViewById(R.id.etWeight);
        etStepGoal = view.findViewById(R.id.etStepGoal);
        etWaterGoal = view.findViewById(R.id.etWaterGoal);
        etActiveGoal = view.findViewById(R.id.etActiveGoal);

        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        llChangePassword = view.findViewById(R.id.llChangePassword);
        
        Button btnSave = view.findViewById(R.id.btnSaveProfile);
        Button btnUpdatePassword = view.findViewById(R.id.btnUpdatePassword);
        TextView tvLogout = view.findViewById(R.id.tvLogout);
        TextView tvChangePassword = view.findViewById(R.id.tvChangePassword);

        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
            String name = prefs.getString("username", "John Doe");
            userEmail = prefs.getString("user_email", "john.doe@email.com");
            tvName.setText(name);
            tvEmail.setText(userEmail);
            
            loadUserData();
        }

        btnSave.setOnClickListener(v -> saveUserData());
        tvLogout.setOnClickListener(v -> logout());

        tvChangePassword.setOnClickListener(v -> {
            if (llChangePassword.getVisibility() == View.GONE) {
                llChangePassword.setVisibility(View.VISIBLE);
            } else {
                llChangePassword.setVisibility(View.GONE);
            }
        });

        btnUpdatePassword.setOnClickListener(v -> updatePassword());

        return view;
    }

    private void updatePassword() {
        String currentPass = etCurrentPassword.getText().toString();
        String newPass = etNewPassword.getText().toString();

        if (currentPass.isEmpty() || newPass.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all password fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getUserByEmail(userEmail);
            if (user != null) {
                if (user.password.equals(currentPass)) {
                    user.password = newPass;
                    userDao.update(user);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                            etCurrentPassword.setText("");
                            etNewPassword.setText("");
                            llChangePassword.setVisibility(View.GONE);
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> 
                            Toast.makeText(getContext(), "Current password incorrect", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void loadUserData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getUserByEmail(userEmail);
            if (user != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (user.height > 0) etHeight.setText(String.valueOf(user.height));
                    if (user.weight > 0) etWeight.setText(String.valueOf(user.weight));
                    etStepGoal.setText(String.valueOf(user.stepGoal));
                    etWaterGoal.setText(String.valueOf(user.waterGoal));
                    etActiveGoal.setText(String.valueOf(user.activeMinGoal));
                });
            }
        });
    }

    private void saveUserData() {
        String heightStr = etHeight.getText().toString();
        String weightStr = etWeight.getText().toString();
        String stepsGoalStr = etStepGoal.getText().toString();
        String waterGoalStr = etWaterGoal.getText().toString();
        String activeGoalStr = etActiveGoal.getText().toString();

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter height and weight", Toast.LENGTH_SHORT).show();
            return;
        }

        float height = Float.parseFloat(heightStr);
        float weight = Float.parseFloat(weightStr);
        int stepGoal = stepsGoalStr.isEmpty() ? 10000 : Integer.parseInt(stepsGoalStr);
        int waterGoal = waterGoalStr.isEmpty() ? 8 : Integer.parseInt(waterGoalStr);
        int activeGoal = activeGoalStr.isEmpty() ? 30 : Integer.parseInt(activeGoalStr);

        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getUserByEmail(userEmail);
            if (user != null) {
                user.height = height;
                user.weight = weight;
                user.stepGoal = stepGoal;
                user.waterGoal = waterGoal;
                user.activeMinGoal = activeGoal;
                userDao.update(user);
            }
        });
        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        if (getActivity() != null) {
            SharedPreferences loginPrefs = getActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
            loginPrefs.edit().clear().apply();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
