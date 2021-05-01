package com.itba.runningMate.fragments.running.services.location;

import com.itba.runningMate.fragments.running.model.Route;

public interface Tracker {

    void startTracking();

    void stopTracking();

    Route querySprint();

    long queryStartTime();

    long queryEndTime();

    float queryDistance();

    long queryElapsedTime();

    long queryPace();

    boolean isTracking();

    void setOnTrackingUpdateListener(OnTrackingUpdateListener listener);

    void removeLocationUpdateListener();
}
