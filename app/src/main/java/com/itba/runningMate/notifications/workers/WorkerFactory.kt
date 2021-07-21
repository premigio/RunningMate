package com.itba.runningMate.notifications.workers

import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import com.itba.runningMate.utils.Constants
import java.util.concurrent.TimeUnit

class WorkerFactory {

    companion object {

        fun getCustomerEngagementWorker(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<NotificationWorker>(7, TimeUnit.DAYS)
                .addTag(Constants.NOTIFICATION_CUSTOMER_ENGAGEMENT_CHANNEL_NAME)
                .setInitialDelay(7, TimeUnit.DAYS)
                .build()
        }
    }

}