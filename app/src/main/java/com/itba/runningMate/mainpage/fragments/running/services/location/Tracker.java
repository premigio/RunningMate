package com.itba.runningMate.mainpage.fragments.running.services.location;

import com.itba.runningMate.mainpage.fragments.running.model.Route;

public interface Tracker {

    void startTracking();

    void stopTracking();

    Route querySprint();

    long queryStartTime();

    float queryDistance();

    long queryElapsedTime();

    long queryPace();

    float queryVelocity();

    boolean isTracking();

    void setOnTrackingUpdateListener(OnTrackingUpdateListener listener);

    void removeLocationUpdateListener();
}
