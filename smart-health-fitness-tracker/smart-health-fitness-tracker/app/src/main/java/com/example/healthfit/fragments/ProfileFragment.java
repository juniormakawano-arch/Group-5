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

    private EditText etHeight, etWeight;
    private TextView tvName, tvEmail;
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
        Button btnSave = view.findViewById(R.id.btnSaveProfile);
        TextView tvLogout = view.findViewById(R.id.tvLogout);

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

        return view;
    }

    private void loadUserData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getUserByEmail(userEmail);
            if (user != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (user.height > 0) etHeight.setText(String.valueOf(user.height));
                    if (user.weight > 0) etWeight.setText(String.valueOf(user.weight));
                });
            }
        });
    }

    private void saveUserData() {
        String heightStr = etHeight.getText().toString();
        String weightStr = etWeight.getText().toString();

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both values", Toast.LENGTH_SHORT).show();
            return;
        }

        float height = Float.parseFloat(heightStr);
        float weight = Float.parseFloat(weightStr);

        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getUserByEmail(userEmail);
            if (user != null) {
                user.height = height;
                user.weight = weight;
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
