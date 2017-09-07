package com.pribumitech.pusherapp.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.pribumitech.pusherapp.ApplicationLoader;

/**
 * Penerima jika ada notifikasi terpakai (jika HP BOOTING)
 * Please JANGAN DIRUBAH
 */
public class NotifBroadcastReceiver extends WakefulBroadcastReceiver {

    private boolean isRegistered;

    public void register(final Context context, IntentFilter filter) {
        if (!isRegistered) {
            context.registerReceiver(this, filter);
            isRegistered = true;
        }
    }

    public void unregister(final Context context) {
        if (isRegistered) {
            context.unregisterReceiver(this);
            isRegistered = false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName cn = new ComponentName(ApplicationLoader.applicationContext.getPackageName(), Notifikasi.class.getName());
        startWakefulService(context, intent.setComponent(cn));
    }
}

