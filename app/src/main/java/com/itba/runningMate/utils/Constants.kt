package com.itba.runningMate.utils

object Constants {
    const val APPLICATION_PACKAGE = "com.itba.runningMate"

    /* TODO:  */
    const val WEIGHT = 76.5f

    /* Notifications */
    const val NOTIFICATION_TRACKING_SERVICE_CHANNEL_ID = "TrackingServiceChannel"
    const val NOTIFICATION_TRACKING_SERVICE_CHANNEL_NAME = "Tracking Service Channel"
    const val NOTIFICATION_TRACKING_SERVICE_ID = 1

    const val NOTIFICATION_CUSTOMER_ENGAGEMENT_CHANNEL_ID =
        "Customer Engagement NotificationChannel"
    const val NOTIFICATION_CUSTOMER_ENGAGEMENT_CHANNEL_NAME =
        "Customer Engagement Notification Channel"
    const val NOTIFICATION_CUSTOMER_ENGAGEMENT_SERVICE_ID = 2

    // TODO: encapsular dentro de una clase Permissions
    /* Permissions */
    const val PERMISSION_LOCATION = 1

    /* Map */
    const val DEFAULT_LATITUDE = -34.606451
    const val DEFAULT_LONGITUDE = -58.4396797
    const val DEFAULT_ZOOM = 10
    const val MY_LOCATION_ZOOM = 15

    /* Tracking Service */
    const val LOCATION_UPDATE_INTERVAL = 5000
    const val LOCATION_UPDATE_FASTEST_INTERVAL = 1000
    const val STOP_WATCH_UPDATE_INTERVAL = 200L
    const val DISTANCE_EPSILON = 0.1
}