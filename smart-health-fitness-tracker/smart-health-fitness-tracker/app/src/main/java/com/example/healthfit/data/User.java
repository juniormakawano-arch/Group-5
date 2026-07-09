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

    public User(@NonNull String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.height = 0;
        this.weight = 0;
        this.streakCount = 0;
    }
}
