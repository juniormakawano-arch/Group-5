package com.example.healthfit.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface AchievementDao {
    @Query("SELECT * FROM achievements")
    LiveData<List<Achievement>> getAllAchievements();

    @Query("SELECT * FROM achievements WHERE isUnlocked = 1")
    LiveData<List<Achievement>> getUnlockedAchievements();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Achievement achievement);

    @Update
    void update(Achievement achievement);
}
