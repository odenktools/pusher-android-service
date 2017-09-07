/**
 * Modified MIT License
 *
 * Copyright 2016 OneSignal
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * 1. The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * 2. All copies of substantial portions of the Software may only be used in connection
 * with services provided by OneSignal.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pribumitech.pusherapp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.pribumitech.pusherapp.ApplicationLoader;
import com.pribumitech.pusherapp.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Abtract Class for handle android notification
 */
public abstract class NotificationExtenderService extends IntentService {

    protected abstract boolean onNotificationProcessing(OSNotificationReceivedResult notification);

    public NotificationExtenderService() {
        super("NotificationExtenderService");
        setIntentRedelivery(true);
    }

    private void processIntent(Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Log.d("ERROR", "No extras sent to NotificationExtenderService in its Intent!\n" + intent);
            //OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "No extras sent to NotificationExtenderService in its Intent!\n" + intent);
            return;
        }
        String messagesNotif = bundle.getString("message");
        String jsonStrPayload = bundle.getString("json_payload");
        if (messagesNotif == null) {
            return;
        }
        JSONObject object = null;
        try {
            object = new JSONObject(jsonStrPayload);
            processJsonObject(object);
        } catch (JSONException e) {
            Log.d("ERROR DECODE", e.getMessage());
        }
        Log.d("PUSHER_RECEIVER", messagesNotif);
        buildNotification(intent, messagesNotif);
    }

    private void processJsonObject(JSONObject currentJsonPayload) {
        OSNotificationReceivedResult receivedResult = new OSNotificationReceivedResult();
        receivedResult.payload = NotificationBundleProcessor.OSNotificationPayloadFrom(currentJsonPayload);
    }

    private void buildNotification(Intent intent, String messagesNotif) {

        NotificationManager mNotificationManager =
                (NotificationManager) ApplicationLoader.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(ApplicationLoader.applicationContext)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_onesignal_default) // Small Icon required or notification doesn't display
                .setContentTitle(ApplicationLoader.applicationContext.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messagesNotif))
                .setContentText(messagesNotif)
                .setDefaults(
                        Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE)
                .setTicker(messagesNotif);

        notifBuilder.setVibrate(new long[]{0, 100, 0, 100});
        PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        notifBuilder.setContentIntent(contentIntent);
        mNotificationManager.cancel(1);

        Notification notification = notifBuilder.build();
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 1000;
        notification.ledOffMS = 1000;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        try {
            mNotificationManager.notify(1, notification);
        } catch (Exception e) {
            //FileLog.e("tmessages", e);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        processIntent(intent);
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }
}
