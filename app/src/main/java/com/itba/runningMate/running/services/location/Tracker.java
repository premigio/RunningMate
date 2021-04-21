package com.itba.runningMate.running.services.location;

import com.itba.runningMate.running.model.Route;

public interface Tracker {

    void startTracking();

    void stopTracking();

    Route querySprint();

    boolean isTracking();

    void setOnLocationUpdateListener(OnLocationUpdateListener listener);

    void removeLocationUpdateListener();
}
