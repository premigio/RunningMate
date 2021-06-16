package com.itba.runningMate.mainpage.fragments.running;

import androidx.annotation.VisibleForTesting;

import com.itba.runningMate.mainpage.fragments.running.RunningView;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.services.location.Tracker;
import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener;

import java.lang.ref.WeakReference;

public class RunningPresenter implements OnTrackingLocationUpdateListener {

    private final WeakReference<RunningView> view;
    private final RunningStateStorage stateStorage;

    private Tracker tracker;
    private boolean isTrackerAttached;

    public RunningPresenter(final RunningStateStorage stateStorage,
                            final RunningView view) {
        this.isTrackerAttached = false;
        this.view = new WeakReference<>(view);
        this.stateStorage = stateStorage;
    }

    public void onViewAttached() {
        if (view.get() == null) {
            return;
        }
        if (!view.get().areLocationPermissionGranted()) {
            view.get().requestLocationPermission();
        } else {
            view.get().launchAndAttachTrackingService();
        }
    }

    public void onViewDetached() {
        stateStorage.persistState();
        if (isTrackerAttached && view.get() != null) {
            tracker.removeTrackingLocationUpdateListener(this);
            view.get().detachTrackingService();
        }
    }

    public void onMapAttached() {
        if (view.get() == null) {
            return;
        }
        if (view.get().areLocationPermissionGranted()) {
            view.get().mapEnableMyLocation();
            if (stateStorage.hasLastKnownLocation()) {
                view.get().showLocation(stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude());
            }
        } else {
            view.get().mapDisableMyLocation();
            view.get().showDefaultLocation();
        }
    }

    public void onTrackingServiceAttached(Tracker tracker) {
        this.tracker = tracker;
        this.isTrackerAttached = true;
        tracker.setOnTrackingLocationUpdateListener(this);
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

    public void onRequestLocationPermissionResult(boolean grantedPermission) {
        if (view.get() == null) {
            return;
        }
        if (grantedPermission) {
            view.get().launchAndAttachTrackingService();
            onMapAttached();
        } else {
            view.get().showLocationPermissionNotGrantedError();
        }
    }

    public void onStartButtonClick() {
        if (view.get() == null) {
            return;
        }
        if (!view.get().areLocationPermissionGranted()) {
            view.get().requestLocationPermission();
        } else {
            if (isTrackerAttached && !tracker.isTracking()) {
                tracker.startTracking();
                view.get().launchRunningActivity();
            }
        }
    }

    @Override
    public void onLocationUpdate(double latitude, double longitude) {
        if (view.get() == null) {
            return;
        }
        if (stateStorage.isCenterCamera()) {
            view.get().showLocation(latitude, longitude);
        }
        stateStorage.setLastKnownLocation(latitude, longitude);
    }

    @VisibleForTesting
    public Tracker getTracker() {
        return this.tracker;
    }

}