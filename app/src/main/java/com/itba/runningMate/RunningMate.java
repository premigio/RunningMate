package com.itba.runningMate;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import timber.log.Timber;


public class RunningMate extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel locationServiceChannel = new NotificationChannel(
                    Constants.NOTIFICATION_LOCATION_SERVICE_CHANNEL__ID,
                    Constants.NOTIFICATION_LOCATION_SERVICE_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(locationServiceChannel);
        }
    }

}
