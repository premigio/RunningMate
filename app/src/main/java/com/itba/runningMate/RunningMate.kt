package com.itba.runningMate

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import com.itba.runningMate.di.DependencyContainerLocator
import com.itba.runningMate.notifications.NotificationChannelFactory
import com.itba.runningMate.notifications.workers.NotificationWorker.Companion.NOTIFICATION_WORKER_NAME
import com.itba.runningMate.notifications.workers.WorkerFactory
import timber.log.Timber
import timber.log.Timber.DebugTree

class RunningMate : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        createNotificationChannels()
        createNotificationWorker()

    }

    private fun createNotificationWorker() {
        val container = DependencyContainerLocator.locateComponent(this)
        container.getWorkManager()
            .enqueueUniquePeriodicWork(
                NOTIFICATION_WORKER_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                WorkerFactory.getCustomerEngagementWorker()
            )
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(NotificationChannelFactory.getTrackingServiceNotificationChannel())
            notificationManager.createNotificationChannel(NotificationChannelFactory.getCustomerEngagementNotificationChannel())
        }

    }

}
