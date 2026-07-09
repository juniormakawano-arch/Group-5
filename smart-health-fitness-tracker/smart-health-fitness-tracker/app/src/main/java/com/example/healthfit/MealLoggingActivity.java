package com.example.healthfit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthfit.data.AppDatabase;
import com.example.healthfit.data.Meal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MealLoggingActivity extends AppCompatActivity {

    private EditText etType, etFood, etCal;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_logging);

        db = AppDatabase.getDatabase(this);
        etType = findViewById(R.id.etMealType);
        etFood = findViewById(R.id.etFoodName);
        etCal = findViewById(R.id.etMealCalories);
        Button btnSave = findViewById(R.id.btnSubmitMeal);

        btnSave.setOnClickListener(v -> saveMeal());
    }

    private void saveMeal() {
        String type = etType.getText().toString();
        String food = etFood.getText().toString();
        String calStr = etCal.getText().toString();

        if (type.isEmpty() || food.isEmpty() || calStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int cal = Integer.parseInt(calStr);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String userEmail = getSharedPreferences("login_prefs", MODE_PRIVATE).getString("user_email", "");

        Executors.newSingleThreadExecutor().execute(() -> {
            Meal meal = new Meal(date, userEmail, type, food, cal);
            db.mealDao().insert(meal);
            runOnUiThread(() -> {
                Toast.makeText(this, "Meal Saved!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
