package com.example.healthfit.data;

import android.content.Context;
import java.util.concurrent.Executors;

public class AchievementManager {
    private final AppDatabase db;
    private final String userEmail;

    public AchievementManager(Context context, String userEmail) {
        this.db = AppDatabase.getDatabase(context);
        this.userEmail = userEmail;
        initializeAchievements();
    }

    private void initializeAchievements() {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.achievementDao().insert(new Achievement("steps_10k", userEmail, "Step Master", "Walk 10,000 steps in a day"));
            db.achievementDao().insert(new Achievement("water_8", userEmail, "Hydration King", "Drink 8 glasses of water in a day"));
            db.achievementDao().insert(new Achievement("active_60", userEmail, "Energy Booster", "Complete 60 minutes of activity"));
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
            Achievement achievement = db.achievementDao().getAchievementById(id, userEmail);
            if (achievement != null && !achievement.isUnlocked) {
                achievement.isUnlocked = true;
                achievement.unlockDate = System.currentTimeMillis();
                db.achievementDao().update(achievement);
            }
        });
    }
}
