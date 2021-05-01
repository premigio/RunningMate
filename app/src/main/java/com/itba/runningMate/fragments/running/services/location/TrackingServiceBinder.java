package com.itba.runningMate.fragments.running.services.location;

import android.os.Binder;

import androidx.lifecycle.LiveData;

import com.itba.runningMate.fragments.running.model.Route;

public class TrackingServiceBinder extends Binder implements Tracker {

    private final TrackingService trackingService;

    public TrackingServiceBinder(final TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    public Route querySprint() {
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

    public void removeLocationUpdateListener() {
        trackingService.removeLocationUpdateListener();
    }

    @Override
    public long queryStartTime() {
        return trackingService.getStartTimeMillis();
    }

    @Override
    public long queryEndTime() {
        return trackingService.getEndTimeMillis();
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
}
