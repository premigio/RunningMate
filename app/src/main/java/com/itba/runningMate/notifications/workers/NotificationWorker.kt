package com.itba.runningMate.notifications.workers

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.itba.runningMate.R
import com.itba.runningMate.di.DependencyContainerLocator
import com.itba.runningMate.domain.Level
import com.itba.runningMate.mainpage.MainPageActivity
import com.itba.runningMate.repository.aggregaterunmetrics.AggregateRunMetricsStorage
import com.itba.runningMate.utils.Constants

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) :
    Worker(appContext, workerParams) {

    private val aggregateRunMetricsStorage: AggregateRunMetricsStorage

    override fun doWork(): Result {
        val totalDistance = aggregateRunMetricsStorage.getTotalDistance()
        val currentLevel = Level.from(totalDistance)
        val remainingLevelKm = currentLevel.minKm + currentLevel.sizeKm - totalDistance

        val notificationMessage = when {
            remainingLevelKm > 0 -> {
                "Let's go for a run!! \nYou are missing %.1fkm to complete level ${
                    applicationContext.getString(
                        currentLevel.title
                    )
                }".format(remainingLevelKm)
            }
            else -> {
                "Let's go for a run!"
            }
        }

        startNotification(notificationMessage)

        return Result.success()
    }

    private fun startNotification(message: String) {
        val notificationIntent = Intent(applicationContext, MainPageActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(applicationContext)
        stackBuilder.addNextIntentWithParentStack(notificationIntent)

        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(
            applicationContext,
            Constants.NOTIFICATION_CUSTOMER_ENGAGEMENT_CHANNEL_ID
        )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setSmallIcon(R.drawable.ic_stat_notify_tracking_service)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .build()

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(Constants.NOTIFICATION_CUSTOMER_ENGAGEMENT_SERVICE_ID, notification)
        }
    }

    init {
        val container = DependencyContainerLocator.locateComponent(applicationContext)
        aggregateRunMetricsStorage = container.getAggregateRunMetricsStorage()
    }

    companion object {
        const val NOTIFICATION_WORKER_NAME = "customer Engagement Notification Worker"
    }

}