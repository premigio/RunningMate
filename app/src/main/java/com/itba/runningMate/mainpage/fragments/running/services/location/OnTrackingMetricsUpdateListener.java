package com.itba.runningMate.mainpage.fragments.running.services.location;

public interface OnTrackingMetricsUpdateListener {

    void onStopWatchUpdate(long elapsedTime);

    void onDistanceUpdate(float elapsedDistance);

    void onPaceUpdate(long pace);

}
