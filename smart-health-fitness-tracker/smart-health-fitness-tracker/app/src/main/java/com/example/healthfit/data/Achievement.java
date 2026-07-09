package com.example.healthfit.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "achievements")
public class Achievement {
    @PrimaryKey
    @NonNull
    public String id;
    public String title;
    public String description;
    public boolean isUnlocked;
    public long unlockDate;

    public Achievement(@NonNull String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isUnlocked = false;
    }
}
