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
    @Query("SELECT * FROM daily_logs WHERE date = :date AND userEmail = :userEmail")
    LiveData<DailyLog> getLogByDate(String date, String userEmail);

    @Query("SELECT * FROM daily_logs WHERE date = :date AND userEmail = :userEmail")
    DailyLog getLogByDateSync(String date, String userEmail);

    @Query("SELECT * FROM daily_logs WHERE userEmail = :userEmail ORDER BY date DESC")
    LiveData<List<DailyLog>> getAllLogsForUser(String userEmail);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DailyLog log);

    @Update
    void update(DailyLog log);
}
