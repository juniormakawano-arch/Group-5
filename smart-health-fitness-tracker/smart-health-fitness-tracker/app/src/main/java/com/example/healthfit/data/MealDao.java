package com.example.healthfit.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import java.util.List;

@Dao
public interface MealDao {
    @Insert
    void insert(Meal meal);

    @Query("SELECT * FROM meals WHERE date = :date AND userEmail = :userEmail")
    LiveData<List<Meal>> getMealsByDate(String date, String userEmail);

    @Query("SELECT SUM(calories) FROM meals WHERE date = :date AND userEmail = :userEmail")
    LiveData<Integer> getTotalCaloriesByDate(String date, String userEmail);
}
