package com.itba.runningMate.fragments.running.services.location;

public interface OnTrackingUpdateListener {

    void onLocationUpdate(double latitude, double longitude);

    void onStopWatchUpdate(long elapsedTime);

    void onDistanceUpdate(float elapsedDistance);

    void onPaceUpdate(long pace);

}
