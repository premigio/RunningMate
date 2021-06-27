package com.itba.runningMate.services.location.listeners;

public interface OnTrackingMetricsUpdateListener {

    void onStopWatchUpdate(long elapsedTime);

    void onDistanceUpdate(float elapsedDistance);

    void onPaceUpdate(long pace);

}
