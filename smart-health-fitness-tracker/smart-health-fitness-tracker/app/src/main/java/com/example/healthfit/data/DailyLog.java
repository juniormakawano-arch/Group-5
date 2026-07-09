package com.example.healthfit.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "daily_logs", primaryKeys = {"date", "userEmail"})
public class DailyLog {
    @NonNull
    public String date; // Format: YYYY-MM-DD

    @NonNull
    public String userEmail;

    public int steps;
    public int water;
    public int floors;
    public int activeMinutes;
    public int sleepHours;
    public int sleepMinutes;
    public int caloriesConsumed;
    public float weight;

    public DailyLog(@NonNull String date, @NonNull String userEmail) {
        this.date = date;
        this.userEmail = userEmail;
    }
}
