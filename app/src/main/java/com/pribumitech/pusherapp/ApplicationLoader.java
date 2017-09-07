package com.pribumitech.pusherapp;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.pribumitech.pusherapp.services.BackgroundService;
import com.pribumitech.pusherapp.services.NotifBroadcastReceiver;

public class ApplicationLoader extends Application {

    @SuppressLint("StaticFieldLeak")
    public static ApplicationLoader Instance = null;

    @SuppressLint("StaticFieldLeak")
    public static Context applicationContext;

    @SuppressLint("StaticFieldLeak")
    public static volatile Handler applicationHandler;

    public static NotifBroadcastReceiver mReceiver = null;

    public static final String CHANNEL_NAME = BuildConfig.PUSHER_CHANNEL_NAME;
    public static final String EVENT_NAME = BuildConfig.PUSHER_EVENT_NAME;
    public static final String TRIGGER_END_POINT = BuildConfig.PUSHER_TRIGGER_END_POINT;
    public static final String PUSHER_END_POINT = BuildConfig.PUSHER_END_POINT;
    public static final String PUSHER_AUTH_END_POINT = BuildConfig.PUSHER_END_POINT +
            BuildConfig.PUSHER_AUTH_END_POINT;

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        applicationContext = getApplicationContext();
        applicationHandler = new Handler(applicationContext.getMainLooper());

        //If You want automatic start service uncomment this code
        //ApplicationLoader.startPusherService();
        if (!ApplicationLoader.isServiceRunning(BackgroundService.class)) {
            ApplicationLoader.startPusherService();
        }
    }

    public static boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void startPusherService() {

        mReceiver = new NotifBroadcastReceiver();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BuildConfig.PUSHER_SERVICE_NAME);
        mReceiver.register(applicationContext, filter);
        //applicationContext.registerReceiver(mReceiver, filter);

        applicationContext.startService(new Intent(applicationContext, BackgroundService.class));
    }

    /**
     * Stop Services
     */
    public static void stopPushService() {

        mReceiver.unregister(applicationContext);
        //applicationContext.unregisterReceiver(mReceiver);
        applicationContext.stopService(new Intent(applicationContext, BackgroundService.class));

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            PendingIntent pintent2 =
                    PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, BackgroundService.class), 0);
            AlarmManager alarm2 = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
            alarm2.cancel(pintent2);
        }
    }
}
