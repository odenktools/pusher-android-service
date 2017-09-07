package com.pribumitech.pusherapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pribumitech.pusherapp.ApplicationLoader;
import com.pribumitech.pusherapp.BuildConfig;
import com.pribumitech.pusherapp.utils.PusherOdk;
import com.pusher.client.channel.PrivateChannelEventListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handling Pusher On Background Service
 */
public class BackgroundService extends Service {

    private Handler handler = new Handler(Looper.getMainLooper());

    Thread readthread;

    private Runnable checkRunnable = new Runnable() {
        @Override
        public void run() {
            check();
        }
    };

    public BackgroundService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        check();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        PusherOdk.getInstance().PusherApp.connect();
        if (PusherOdk.getInstance()
                .PusherApp.getPrivateChannel(ApplicationLoader.CHANNEL_NAME) == null) {
            subsribe();
        }
        return START_STICKY;
    }

    private void subsribe() {
        PusherOdk.getInstance()
                .PusherApp.subscribePrivate(ApplicationLoader.CHANNEL_NAME,
                new PrivateChannelEventListener() {
                    @Override
                    public void onAuthenticationFailure(String s, Exception e) {
                        Log.d("PUSHER_AUTH_FAILURE", e.getMessage());
                    }

                    @Override
                    public void onSubscriptionSucceeded(String channelName) {
                        Log.d("PUSHER", "Channel subscription " + channelName);
                    }

                    @Override
                    public void onEvent(final String s, final String s2, final String s3) {
                        Log.d("PUSHER_EVENT", s3);
                        readthread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Intent broadcastIntent = new Intent();
                                broadcastIntent.setAction(BuildConfig.PUSHER_SERVICE_NAME);
                                broadcastIntent.putExtra("json_payload", s3);
                                broadcastIntent.putExtra("message", s3);
                                sendBroadcast(broadcastIntent);
                            }
                        });
                        readthread.start();
                    }
                }, ApplicationLoader.EVENT_NAME);
    }

    private void check() {
        handler.removeCallbacks(checkRunnable);
        handler.postDelayed(checkRunnable, 1500);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
