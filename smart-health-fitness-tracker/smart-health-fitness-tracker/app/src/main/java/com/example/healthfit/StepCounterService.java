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
            int totalSteps = (int) event.values[0];
            if (initialSteps == -1) {
                initialSteps = totalSteps;
            }
            // In a real app, you'd calculate the delta and update the database
            // For this demo, let's just log the event.
            updateStepsInDb(totalSteps);
        }
    }

    private void updateStepsInDb(int totalSteps) {
        executorService.execute(() -> {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            DailyLog log = logDao.getLogByDateSync(date);
            if (log != null) {
                // This is simplified logic
                // log.steps += delta;
                // logDao.update(log);
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
