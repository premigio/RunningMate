package com.itba.runningMate.mainpage.fragments.running.services.location;

import android.os.Binder;

import com.itba.runningMate.mainpage.fragments.running.model.Route;
import com.itba.runningMate.utils.run.RunMetrics;

public class TrackingServiceBinder extends Binder implements Tracker {

    private final TrackingService trackingService;

    public TrackingServiceBinder(final TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    public Route queryRoute() {
        return new Route().addLatLngsToRoute(trackingService.getTrackedLocations());
    }

    public boolean isTracking() {
        return trackingService.isTracking();
    }

    public void startTracking() {
        trackingService.startTracking();
    }

    public void stopTracking() {
        trackingService.stopTracking();
    }

    public void setOnTrackingUpdateListener(OnTrackingUpdateListener listener) {
        trackingService.setOnTrackingUpdateListener(listener);
    }

    @Override
    public void setOnTrackingLocationUpdateListener(OnTrackingLocationUpdateListener listener) {
        trackingService.setOnTrackingLocationUpdateListener(listener);
    }

    @Override
    public void setTrackingMetricsUpdateListener(OnTrackingMetricsUpdateListener listener) {
        trackingService.setOnTrackingMetricsUpdateListener(listener);
    }

    @Override
    public void removeTrackingUpdateListener(OnTrackingUpdateListener listener) {
        trackingService.removeTrackingUpdateListener(listener);
    }

    @Override
    public void removeTrackingLocationUpdateListener(OnTrackingLocationUpdateListener listener) {
        trackingService.removeTrackingLocationUpdateListener(listener);
    }

    @Override
    public void removeTrackingMetricsUpdateListener(OnTrackingMetricsUpdateListener listener) {
        trackingService.removeTrackingMetricsUpdateListener(listener);
    }

    @Override
    public long queryStartTime() {
        return trackingService.getStartTimeMillis();
    }

    @Override
    public float queryDistance() {
        return trackingService.getElapsedDistance();
    }

    @Override
    public long queryElapsedTime() {
        return trackingService.getElapsedMillis();
    }

    @Override
    public long queryPace() {
        return trackingService.getPace();
    }

    @Override
    public float queryVelocity() {
        return RunMetrics.calculateVelocity(trackingService.getElapsedDistance(), trackingService.getElapsedMillis());
    }
}
