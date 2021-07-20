package com.itba.runningMate.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import com.itba.runningMate.utils.Constants

class NotificationChannelFactory {

    companion object {

        fun getTrackingServiceNotificationChannel(): NotificationChannel {
            return NotificationChannel(
                Constants.NOTIFICATION_TRACKING_SERVICE_CHANNEL_ID,
                Constants.NOTIFICATION_TRACKING_SERVICE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
        }

        fun getCustomerEngagementNotificationChannel(): NotificationChannel {
            return NotificationChannel(
                Constants.NOTIFICATION_CUSTOMER_ENGAGEMENT_CHANNEL_ID,
                Constants.NOTIFICATION_CUSTOMER_ENGAGEMENT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        }

    }
}