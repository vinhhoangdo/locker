package com.example.locker.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.locker.MainActivity;
import com.example.locker.R;
import com.example.locker.database.LockPackageDatabase;
import com.example.locker.util.Constant;
import com.example.locker.util.SharedPreferencesHelper;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AppLaunchDetectionService extends Service {
    LockPackageDatabase database;
    private String currentActivity = "";
    private boolean isAppRunning = false;
    SharedPreferencesHelper sharedPreferencesHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
        database = new LockPackageDatabase(getApplicationContext());
        scheduleMethod();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startDetectForeground();
        } else {
            startForeground(1, new Notification());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startDetectForeground() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
        String channelName = "Background Service";
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.GREEN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.icon_lock_item)
                .setContentTitle("Background running")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    private void scheduleMethod() {
        ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();
        scheduledService.scheduleAtFixedRate(this::checkRunningApps,
                1000, 1000, TimeUnit.MILLISECONDS
        );
    }

    public void checkRunningApps() {
        String activityOnTop = getProcessName(this);
        if (!activityOnTop.equals("")) {
            if (database.getPackageName(activityOnTop)) {
                if (!activityOnTop.equals(currentActivity) && !isAppRunning) {
                    ActivityManager manager = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
                    String type = sharedPreferencesHelper.getLockType();
                    if (type.equalsIgnoreCase(Constant.PASSCODE_TYPE)) {
                        startService(new Intent(AppLaunchDetectionService.this, PasscodeService.class));
                        isAppRunning = true;
                        currentActivity = activityOnTop;
                    } else if (type.equalsIgnoreCase(Constant.PATTERN_TYPE)) {
                        startService(new Intent(AppLaunchDetectionService.this, PatternService.class));
                        isAppRunning = true;
                        currentActivity = activityOnTop;
                    }
                }
            } else {
                isAppRunning = false;
                currentActivity = "";
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public static String getProcessName(Context context) {
        String foregroundProcess = "";
        UsageStatsManager mUsageStatsManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            mUsageStatsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);
        }
        long time = System.currentTimeMillis();
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
        if (stats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                String topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();

                foregroundProcess = topPackageName;
            }
        }
        Log.i("packageName", foregroundProcess + "");

        return foregroundProcess;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("task", "TASK REMOVED");

        Intent intent = new Intent(getApplicationContext(), AppLaunchDetectionService.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getService(this, 1001, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1000, pendingIntent);
        super.onTaskRemoved(rootIntent);
    }

}
