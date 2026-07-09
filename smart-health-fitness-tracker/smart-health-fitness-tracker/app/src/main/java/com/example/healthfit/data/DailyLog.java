package com.example.healthfit.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "daily_logs")
public class DailyLog {
    @PrimaryKey
    @NonNull
    public String date; // Format: YYYY-MM-DD

    public int steps;
    public int water;
    public int floors;
    public int activeMinutes;
    public int sleepHours;
    public int sleepMinutes;
    public int caloriesConsumed;
    public float weight;

    public DailyLog(@NonNull String date) {
        this.date = date;
    }
}
