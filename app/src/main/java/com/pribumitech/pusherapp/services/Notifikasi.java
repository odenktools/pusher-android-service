package com.pribumitech.pusherapp.services;

/**
 * Class Notifikasi
 * Ini harus ter-registrasi pada AndroidManifest.xml
 */
public class Notifikasi extends NotificationExtenderService {

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        return false;
    }
}
