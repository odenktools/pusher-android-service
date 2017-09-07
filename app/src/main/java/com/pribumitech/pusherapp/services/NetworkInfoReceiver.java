package com.pribumitech.pusherapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkInfoReceiver extends BroadcastReceiver {

    private String currentlyConnectedType = "";

    private NetworkListener networkListener = null;

    private boolean isRegistered;

    public NetworkInfoReceiver() {

    }

    public void setNetworkOnChange(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

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
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager mgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        String connectionType = "";

        for (final NetworkInfo info : mgr.getAllNetworkInfo()) {
            if (info.isConnected()) {
                connectionType = info.getTypeName();
                break;
            }
        }

        if (!currentlyConnectedType.equals(connectionType)) {
            currentlyConnectedType = connectionType;
            if (networkListener != null) {
                networkListener.updateNetStatus(connectionType);
            }
        }
    }

    public interface NetworkListener {
        void updateNetStatus(final String connectionType);
    }
}