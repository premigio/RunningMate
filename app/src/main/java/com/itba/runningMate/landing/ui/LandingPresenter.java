package com.itba.runningMate.landing.ui;

import com.itba.runningMate.landing.model.Route;
import com.itba.runningMate.landing.repository.LandingStateStorage;
import com.itba.runningMate.landing.services.location.OnLocationUpdateListener;
import com.itba.runningMate.landing.services.location.Tracker;

import java.lang.ref.WeakReference;

public class LandingPresenter implements OnLocationUpdateListener {

    private final WeakReference<LandingView> view;
    private final LandingStateStorage stateStorage;

    private Tracker tracker;
    private boolean isTrackerAttached;

    public LandingPresenter(LandingView view, LandingStateStorage stateStorage) {
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
        if (view.get() != null) {
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
        tracker.setOnLocationUpdateListener(this);
        if (tracker.isTracking() && view.get() != null) {
            // recuperamos la ruta y actualizamos LastKnownLocation
            Route route = tracker.querySprint();
            if (!route.isEmpty()) {
                stateStorage.setLastKnownLocation(route.getLastLatitude(), route.getLastLongitude());
                view.get().showRoute(route);
            }
        }
    }

    public void onTrackingServiceDetached() {
        /*
            TODO: esto puede ser que este en null?, ni idea el tiempo lo determinara
            En teoria no hace falta ni llamar a este metodo por que por detras es una weak reference
         */
        tracker.removeLocationUpdateListener();
        this.tracker = null;
        this.isTrackerAttached = false;
    }

    public void startRun() {
        if (view.get() != null && !view.get().areLocationPermissionGranted()) {
            view.get().requestLocationPermission();
        } else {
            if (isTrackerAttached) {
                tracker.startTracking();
            }
        }
    }

    public void stopRun() {
        if (view.get() != null && !view.get().areLocationPermissionGranted()) {
            view.get().requestLocationPermission();
        } else {
            if (isTrackerAttached) {
                tracker.stopTracking();
            }
        }
    }

    public void centerCamera() {
        stateStorage.setCenterCamera(true);
    }

    public void freeCamera() {
        stateStorage.setCenterCamera(false);
    }

    void onRequestLocationPermissionResult(boolean grantedPermission) {
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

    private boolean compareLocations(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        return Double.compare(latitudeA, latitudeB) == 0 && Double.compare(longitudeA, longitudeB) == 0;
    }
}
