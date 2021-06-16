package com.itba.runningMate.running.fragments.map;

import androidx.annotation.VisibleForTesting;

import com.itba.runningMate.domain.Route;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener;
import com.itba.runningMate.services.location.Tracker;

import java.lang.ref.WeakReference;

public class RunningMapPresenter implements OnTrackingLocationUpdateListener {

    private final WeakReference<RunningMapView> view;
    private final RunningStateStorage stateStorage;

    private Tracker tracker;
    private boolean isTrackerAttached;

    public RunningMapPresenter(final RunningStateStorage stateStorage,
                               final RunningMapView view) {
        this.isTrackerAttached = false;
        this.view = new WeakReference<>(view);
        this.stateStorage = stateStorage;
    }

    public void onViewAttached() {
        if (view.get() == null) {
            return;
        }
        view.get().attachTrackingService();
    }

    public void onViewDetached() {
        stateStorage.persistState();
        if (isTrackerAttached) {
            tracker.removeTrackingLocationUpdateListener(this);
        }
        if (view.get() != null) {
            view.get().detachTrackingService();
        }
    }

    public void onMapAttached() {
        if (view.get() == null) {
            return;
        }
        if (stateStorage.hasLastKnownLocation()) {
            view.get().showLocation(stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude());
        } else {
            view.get().showDefaultLocation();
        }
    }

    public void onTrackingServiceAttached(Tracker tracker) {
        this.tracker = tracker;
        this.isTrackerAttached = true;
        tracker.setOnTrackingLocationUpdateListener(this);
        if (tracker.isTracking() && view.get() != null) {
            // recuperamos la ruta y actualizamos LastKnownLocation
            Route route = tracker.queryRoute();
            if (!route.isEmpty()) {
                stateStorage.setLastKnownLocation(route.getLastLatitude(), route.getLastLongitude());
                view.get().showRoute(route);
            }
        }
    }

    public void onTrackingServiceDetached() {
        this.tracker = null;
        this.isTrackerAttached = false;
    }

    public void centerCamera() {
        stateStorage.setCenterCamera(true);
        if (stateStorage.hasLastKnownLocation()) {
            view.get().showLocation(stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude());
        }
    }

    public void freeCamera() {
        stateStorage.setCenterCamera(false);
    }

    @Override
    public void onLocationUpdate(double latitude, double longitude) {
        if (isTrackerAttached && tracker.isTracking() && view.get() != null) {
            if (stateStorage.hasLastKnownLocation()) {
                Route route = new Route()
                        .addToRoute(stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude())
                        .addToRoute(latitude, longitude);
                view.get().showRoute(route);
            }
        }
        if (stateStorage.isCenterCamera() && view.get() != null) {
            view.get().showLocation(latitude, longitude);
        }
        stateStorage.setLastKnownLocation(latitude, longitude);
    }

    @VisibleForTesting
    public Tracker getTracker() {
        return tracker;
    }

}
