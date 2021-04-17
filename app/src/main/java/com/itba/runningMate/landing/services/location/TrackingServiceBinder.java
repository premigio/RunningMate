package com.itba.runningMate.landing.services.location;

import android.os.Binder;

import com.itba.runningMate.landing.model.Route;

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

    public void setOnLocationUpdateListener(OnLocationUpdateListener listener) {
        trackingService.setOnLocationUpdateListener(listener);
    }

    public void removeLocationUpdateListener() {
        trackingService.removeLocationUpdateListener();
    }

}
