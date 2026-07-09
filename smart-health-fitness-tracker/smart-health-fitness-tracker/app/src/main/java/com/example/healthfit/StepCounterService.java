package com.example.healthfit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.healthfit.data.AppDatabase;
import com.example.healthfit.data.DailyLog;
import com.example.healthfit.data.LogDao;
import com.example.healthfit.data.AchievementManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StepCounterService extends Service implements SensorEventListener {

    private static final String CHANNEL_ID = "StepCounterChannel";
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private LogDao logDao;
    private ExecutorService executorService;
    private AchievementManager achievementManager;
    private int initialSteps = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        logDao = AppDatabase.getDatabase(this).logDao();
        executorService = Executors.newSingleThreadExecutor();
        achievementManager = new AchievementManager(this);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Smart Health Tracker")
                .setContentText("Tracking your steps in background")
                .setSmallIcon(R.drawable.ic_launcher)
                .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalStepsSinceBoot = (int) event.values[0];
            updateStepsInDb(totalStepsSinceBoot);
        }
    }

    private void updateStepsInDb(int totalStepsSinceBoot) {
        executorService.execute(() -> {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            DailyLog log = logDao.getLogByDateSync(date);
            
            // Get last recorded total steps to calculate delta
            int lastBootSteps = getSharedPreferences("step_prefs", MODE_PRIVATE).getInt("last_boot_steps", -1);
            
            if (lastBootSteps == -1) {
                // First time running or after reboot
                getSharedPreferences("step_prefs", MODE_PRIVATE).edit().putInt("last_boot_steps", totalStepsSinceBoot).apply();
                return;
            }

            int delta = totalStepsSinceBoot - lastBootSteps;
            if (delta > 0) {
                if (log == null) {
                    log = new DailyLog(date);
                    log.steps = delta;
                    logDao.insert(log);
                } else {
                    log.steps += delta;
                    logDao.update(log);
                }
                achievementManager.checkAchievements(log);
                getSharedPreferences("step_prefs", MODE_PRIVATE).edit().putInt("last_boot_steps", totalStepsSinceBoot).apply();
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Step Counter Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
