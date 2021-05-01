package com.itba.runningMate.fragments.running.ui;

import android.annotation.SuppressLint;

import com.itba.runningMate.domain.Sprint;
import com.itba.runningMate.fragments.running.model.Route;
import com.itba.runningMate.fragments.running.repository.LandingStateStorage;
import com.itba.runningMate.fragments.running.services.location.OnTrackingUpdateListener;
import com.itba.runningMate.fragments.running.services.location.Tracker;
import com.itba.runningMate.repository.sprint.SprintRepository;
import com.itba.runningMate.utils.sprint.SprintMetrics;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RunningPresenter implements OnTrackingUpdateListener {

    private final WeakReference<RunningView> view;
    private final LandingStateStorage stateStorage;
    private final SprintRepository sprintRepository;

    private Tracker tracker;
    private boolean isTrackerAttached;

    public RunningPresenter(final LandingStateStorage stateStorage, final SprintRepository sprintRepository, final RunningView view) {
        this.isTrackerAttached = false;
        this.view = new WeakReference<>(view);
        this.stateStorage = stateStorage;
        this.sprintRepository = sprintRepository;
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
        tracker.setOnTrackingUpdateListener(this);
        if (tracker.isTracking() && view.get() != null) {
            // recuperamos la ruta y actualizamos LastKnownLocation
            Route route = tracker.querySprint();
            if (!route.isEmpty()) {
                stateStorage.setLastKnownLocation(route.getLastLatitude(), route.getLastLongitude());
                view.get().showRoute(route);
                onPaceUpdate(tracker.queryPace());
                onDistanceUpdate(tracker.queryDistance());
                onStopWatchUpdate(tracker.queryElapsedTime());
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
            if (isTrackerAttached && !tracker.isTracking()) {
                tracker.startTracking();
            }
        }
    }

    public void stopRun() {
        if (view.get() != null && !view.get().areLocationPermissionGranted()) {
            view.get().requestLocationPermission();
        } else {
            if (isTrackerAttached && tracker.isTracking()) {
                tracker.stopTracking();
                float distKm = SprintMetrics.calculateDistance(tracker.querySprint().getLocations());
                long timeMillis = tracker.queryEndTime() - tracker.queryStartTime();
                sprintRepository.insertSprint(new Sprint()
                        .startTime(new Date(tracker.queryStartTime()))
                        .endTime(new Date(tracker.queryEndTime()))
                        .time(tracker.queryEndTime() - tracker.queryStartTime())
                        .route(tracker.querySprint().getLocations())
                        .distance(distKm)
                        .pace(SprintMetrics.calculatePace(distKm, timeMillis))
                        .velocity(SprintMetrics.calculateVelocity(distKm, timeMillis))
                );
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

    @Override
    public void onStopWatchUpdate(long elapsedTime) {
        if (view.get() == null) {
            return;
        }
        view.get().updateStopwatchTextView(hmsTimeFormatter(elapsedTime));
    }

    @Override
    public void onDistanceUpdate(float elapsedDistance) {
        if (view.get() == null) {
            return;
        }
        view.get().updateDistanceTextView(String.format("%.2f", elapsedDistance));
    }

    @Override
    public void onPaceUpdate(long pace) {
        if (view.get() == null) {
            return;
        }
        view.get().updatePaceTextView(hmsTimeFormatter(pace));
    }

    private boolean compareLocations(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        return Double.compare(latitudeA, latitudeB) == 0 && Double.compare(longitudeA, longitudeB) == 0;
    }

    @SuppressLint("DefaultLocale")
    private String hmsTimeFormatter(long millis) {
        return String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

}
