package com.example.healthfit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthfit.data.AppDatabase;
import com.example.healthfit.data.User;
import com.example.healthfit.data.UserDao;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple login screen.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "login_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USERNAME = "username";

    private EditText etEmail, etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView tvSignUp;
    private SharedPreferences prefs;
    private UserDao userDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userDao = AppDatabase.getDatabase(this).userDao();

        // Check if user is already logged in
        if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            String savedUsername = prefs.getString(KEY_USERNAME, "User");
            goToDashboard(savedUsername);
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        setupSignUpLink();

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void setupSignUpLink() {
        String text = "Don't have an account? Sign Up";
        SpannableString ss = new SpannableString(text);
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.parseColor("#2196F3"));
        ss.setSpan(blueSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSignUp.setText(ss);
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            User user = userDao.login(email, password);
            if (user != null) {
                saveLoginState(user.email, user.name);
                runOnUiThread(() -> goToDashboard(user.name));
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void saveLoginState(String email, String name) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USERNAME, name);
        editor.apply();
    }

    private void goToDashboard(String username) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}
