package com.example.healthfit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthfit.data.DailyLog;
import com.example.healthfit.data.DailyLogViewModel;
import androidx.lifecycle.ViewModelProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SleepActivity extends AppCompatActivity {

    private EditText etSleepHours, etSleepMinutes;
    private RatingBar rbSleepQuality;
    private DailyLogViewModel viewModel;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        etSleepHours = findViewById(R.id.etSleepHours);
        etSleepMinutes = findViewById(R.id.etSleepMinutes);
        rbSleepQuality = findViewById(R.id.rbSleepQuality);
        
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        viewModel = new ViewModelProvider(this).get(DailyLogViewModel.class);

        findViewById(R.id.btnSaveSleep).setOnClickListener(v -> {
            String hoursStr = etSleepHours.getText().toString();
            String minutesStr = etSleepMinutes.getText().toString();

            if (hoursStr.isEmpty() || minutesStr.isEmpty()) {
                Toast.makeText(this, "Please enter sleep duration", Toast.LENGTH_SHORT).show();
                return;
            }

            int hours = Integer.parseInt(hoursStr);
            int minutes = Integer.parseInt(minutesStr);
            float quality = rbSleepQuality.getRating();

            saveSleepData(hours, minutes, quality);
            Toast.makeText(this, "Sleep logged successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveSleepData(int hours, int minutes, float quality) {
        viewModel.getLogByDate(currentDate).observe(this, log -> {
            if (log != null) {
                log.sleepHours = hours;
                log.sleepMinutes = minutes;
                // You could add quality to the entity if needed
                viewModel.update(log);
            }
        });
    }
}
