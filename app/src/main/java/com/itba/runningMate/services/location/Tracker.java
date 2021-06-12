package com.itba.runningMate.services.location;

import com.itba.runningMate.domain.Route;
import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener;
import com.itba.runningMate.services.location.listeners.OnTrackingMetricsUpdateListener;
import com.itba.runningMate.services.location.listeners.OnTrackingUpdateListener;

public interface Tracker {

    void startTracking();

    void stopTracking();

    void newLap();

    void resumeTracking();

    Route queryRoute();

    long queryStartTime();

    long queryEndTime();

    float queryDistance();

    long queryElapsedTime();

    long queryPace();

    float queryVelocity();

    boolean isTracking();

    void setOnTrackingUpdateListener(OnTrackingUpdateListener listener);

    void setOnTrackingLocationUpdateListener(OnTrackingLocationUpdateListener listener);

    void setTrackingMetricsUpdateListener(OnTrackingMetricsUpdateListener listener);

    void removeTrackingUpdateListener(OnTrackingUpdateListener listener);

    void removeTrackingLocationUpdateListener(OnTrackingLocationUpdateListener listener);

    void removeTrackingMetricsUpdateListener(OnTrackingMetricsUpdateListener listener);
}
