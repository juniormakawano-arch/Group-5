package com.example.healthfit.data;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DailyLogViewModel extends AndroidViewModel {
    private LogDao logDao;
    private ExecutorService executorService;

    public DailyLogViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        logDao = db.logDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<DailyLog> getLogByDate(String date, String userEmail) {
        return logDao.getLogByDate(date, userEmail);
    }

    public LiveData<List<DailyLog>> getAllLogs(String userEmail) {
        return logDao.getAllLogsForUser(userEmail);
    }

    public void insert(DailyLog log) {
        executorService.execute(() -> logDao.insert(log));
    }

    public void update(DailyLog log) {
        executorService.execute(() -> logDao.update(log));
    }
}
