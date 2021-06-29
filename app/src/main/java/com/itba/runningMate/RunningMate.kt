package com.itba.runningMate

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.itba.runningMate.utils.Constants
import timber.log.Timber
import timber.log.Timber.DebugTree

class RunningMate : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val locationServiceChannel = NotificationChannel(
                Constants.NOTIFICATION_LOCATION_SERVICE_CHANNEL__ID,
                Constants.NOTIFICATION_LOCATION_SERVICE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(locationServiceChannel)
        }
    }
}