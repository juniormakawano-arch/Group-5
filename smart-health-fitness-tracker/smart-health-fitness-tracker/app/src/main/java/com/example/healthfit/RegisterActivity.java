package com.example.healthfit;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthfit.data.AppDatabase;
import com.example.healthfit.data.User;
import com.example.healthfit.data.UserDao;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvBackToLogin;
    private UserDao userDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDao = AppDatabase.getDatabase(this).userDao();

        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        setupLoginLink();

        btnRegister.setOnClickListener(v -> registerUser());
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void setupLoginLink() {
        String text = "Already have an account? Log In";
        SpannableString ss = new SpannableString(text);
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.parseColor("#2196F3"));
        ss.setSpan(blueSpan, 25, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvBackToLogin.setText(ss);
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            User existingUser = userDao.getUserByEmail(email);
            if (existingUser != null) {
                runOnUiThread(() -> Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show());
            } else {
                userDao.insert(new User(email, name, password));
                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
}
