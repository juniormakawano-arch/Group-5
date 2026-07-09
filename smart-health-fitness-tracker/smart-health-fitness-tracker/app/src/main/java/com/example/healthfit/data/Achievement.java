package com.example.healthfit.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "achievements", primaryKeys = {"id", "userEmail"})
public class Achievement {
    @NonNull
    public String id;
    @NonNull
    public String userEmail;
    public String title;
    public String description;
    public boolean isUnlocked;
    public long unlockDate;

    public Achievement(@NonNull String id, @NonNull String userEmail, String title, String description) {
        this.id = id;
        this.userEmail = userEmail;
        this.title = title;
        this.description = description;
        this.isUnlocked = false;
    }
}
