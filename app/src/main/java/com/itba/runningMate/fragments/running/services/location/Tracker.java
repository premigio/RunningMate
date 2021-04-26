package com.itba.runningMate.fragments.running.services.location;

import com.itba.runningMate.fragments.running.model.Route;

public interface Tracker {

    void startTracking();

    void stopTracking();

    Route querySprint();

    boolean isTracking();

    void setOnLocationUpdateListener(OnLocationUpdateListener listener);

    void removeLocationUpdateListener();
}
