package com.example.healthfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Simple login screen.
 * NOTE: This is a placeholder authentication flow for the group project.
 * Replace with real authentication (Firebase Auth, a backend API, etc.)
 * once your team decides on a data/auth strategy.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;

    // Fixed: Method is now inside the class
    public void newMethod() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Placeholder check - swap for real auth later
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}