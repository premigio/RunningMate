package com.itba.runningMate;

public class Constants {

    public static final String APPLICATION_PACKAGE = "com.itba.runningMate";

    // TODO: encapsular dentro de una clase Notifications
    /* Notifications */
    public static final String NOTIFICATION_LOCATION_SERVICE_CHANNEL__ID = "LocationServiceChannel";
    public static final String NOTIFICATION_LOCATION_SERVICE_CHANNEL_NAME = "Location Service Channel";
    public static final int NOTIFICATION_LOCATION_SERVICE_ID = 1;

    // TODO: encapsular dentro de una clase Permissions
    /* Permissions */
    public static final int PERMISSION_LOCATION = 1;

    /* Map */
    public static final double DEFAULT_LATITUDE = -34.606451;
    public static final double DEFAULT_LONGITUDE = -58.4396797;
    public static final int DEFAULT_ZOOM = 10;
    public static final int MY_LOCATION_ZOOM = 15;

    /* Tracking Service */
    public static final int LOCATION_UPDATE_INTERVAL = 5000;
    public static final int LOCATION_UPDATE_FASTEST_INTERVAL = 1000;
    public static final long STOP_WATCH_UPDATE_INTERVAL = 100L;

}
