package com.itba.runningMate.landing.services.location;

import com.itba.runningMate.landing.model.Route;

public interface Tracker {

    void startTracking();

    void stopTracking();

    Route querySprint();

    boolean isTracking();

    void setOnLocationUpdateListener(OnLocationUpdateListener listener);

    void removeLocationUpdateListener();
}
