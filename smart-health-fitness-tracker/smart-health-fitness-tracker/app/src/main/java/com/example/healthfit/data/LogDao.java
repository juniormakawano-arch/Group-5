package com.example.healthfit.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LogDao {
    @Query("SELECT * FROM daily_logs WHERE date = :date")
    LiveData<DailyLog> getLogByDate(String date);

    @Query("SELECT * FROM daily_logs WHERE date = :date")
    DailyLog getLogByDateSync(String date);

    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    LiveData<List<DailyLog>> getAllLogs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailyLog log);

    @Update
    void update(DailyLog log);
}
