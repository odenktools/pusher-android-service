package com.pribumitech.pusherapp.utils;

import com.pribumitech.pusherapp.ApplicationLoader;
import com.pribumitech.pusherapp.BuildConfig;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.util.HttpAuthorizer;

public class PusherOdk {

    private static PusherOdk ourInstance;
    public Pusher PusherApp = null;

    private PusherOdk() {
        final HttpAuthorizer authorizer = new
                HttpAuthorizer(ApplicationLoader.PUSHER_AUTH_END_POINT);
        PusherOptions pusherOptions = new PusherOptions();
        pusherOptions.setAuthorizer(authorizer);
        pusherOptions.setEncrypted(true);
        PusherApp = new Pusher(BuildConfig.PUSHER_API_KEY, pusherOptions);
    }

    /**
     * Singleton Instance
     *
     * @return PusherOdk
     */
    public static synchronized PusherOdk getInstance() {
        if (ourInstance == null) {
            ourInstance = new PusherOdk();
        }
        return ourInstance;
    }

    public String getConnectionId(){
        return PusherApp.getConnection().getSocketId();
    }

    /*public Pusher getPusherApp() {
        if (this.PusherApp == null) {
            this.PusherApp = new Pusher(BuildConfig.PUSHER_API_KEY,
                    pusherOptions);
        }
        return this.PusherApp;
    }*/
}
