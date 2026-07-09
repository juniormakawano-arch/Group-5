package com.example.healthfit.data;

import android.content.Context;
import java.util.concurrent.Executors;

public class AchievementManager {
    private final AppDatabase db;

    public AchievementManager(Context context) {
        this.db = AppDatabase.getDatabase(context);
        initializeAchievements();
    }

    private void initializeAchievements() {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.achievementDao().insert(new Achievement("steps_10k", "Step Master", "Walk 10,000 steps in a day"));
            db.achievementDao().insert(new Achievement("water_8", "Hydration King", "Drink 8 glasses of water in a day"));
            db.achievementDao().insert(new Achievement("active_60", "Energy Booster", "Complete 60 minutes of activity"));
        });
    }

    public void checkAchievements(DailyLog log) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (log.steps >= 10000) unlock("steps_10k");
            if (log.water >= 8) unlock("water_8");
            if (log.activeMinutes >= 60) unlock("active_60");
        });
    }

    private void unlock(String id) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Find achievement and mark as unlocked
            // This is a simplified check
        });
    }
}
