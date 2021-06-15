package com.itba.runningMate.services.location;

import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener;
import com.itba.runningMate.services.location.listeners.OnTrackingMetricsUpdateListener;
import com.itba.runningMate.services.location.listeners.OnTrackingUpdateListener;

public interface TrackingLocationUpdatesDispatcher {

    void setOnTrackingUpdateListener(OnTrackingUpdateListener listener);

    void setOnTrackingLocationUpdateListener(OnTrackingLocationUpdateListener listener);

    void setOnTrackingMetricsUpdateListener(OnTrackingMetricsUpdateListener listener);

    void removeTrackingUpdateListener(OnTrackingUpdateListener listener);

    void removeTrackingLocationUpdateListener(OnTrackingLocationUpdateListener listener);

    void removeTrackingMetricsUpdateListener(OnTrackingMetricsUpdateListener listener);

    void callbackLocationUpdate(double latitude, double longitude);

    void callbackDistanceUpdate(float distance);

    void callbackPaceUpdate(long pace);

    void callbackStopWatchUpdate(long elapsedTime);

    boolean areMetricsUpdatesListener();

    boolean areLocationUpdatesListener();

    boolean areUpdatesListener();
}
