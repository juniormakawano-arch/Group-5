package com.example.healthfit.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "meals")
public class Meal {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String date; // YYYY-MM-DD
    public String type; // Breakfast, Lunch, Dinner, Snack
    public String name;
    public int calories;

    public Meal(@NonNull String date, String type, String name, int calories) {
        this.date = date;
        this.type = type;
        this.name = name;
        this.calories = calories;
    }
}
