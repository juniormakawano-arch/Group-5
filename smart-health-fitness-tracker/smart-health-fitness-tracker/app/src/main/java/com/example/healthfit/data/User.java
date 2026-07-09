package com.example.healthfit.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String email;
    
    public String name;
    public String password;
    public float height; // in cm
    public float weight; // in kg
    public int streakCount;

    // Goals
    public int stepGoal;
    public int waterGoal;
    public int activeMinGoal;

    public User(@NonNull String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.height = 0;
        this.weight = 0;
        this.streakCount = 0;
        this.stepGoal = 10000;
        this.waterGoal = 8;
        this.activeMinGoal = 30;
    }
}
