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
    @Query("SELECT * FROM achievements WHERE userEmail = :userEmail")
    LiveData<List<Achievement>> getAllAchievementsForUser(String userEmail);

    @Query("SELECT * FROM achievements WHERE userEmail = :userEmail AND isUnlocked = 1")
    LiveData<List<Achievement>> getUnlockedAchievementsForUser(String userEmail);

    @Query("SELECT * FROM achievements WHERE id = :id AND userEmail = :userEmail LIMIT 1")
    Achievement getAchievementById(String id, String userEmail);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Achievement achievement);

    @Update
    void update(Achievement achievement);
}
