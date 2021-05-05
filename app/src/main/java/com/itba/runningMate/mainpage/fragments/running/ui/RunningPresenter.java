package com.itba.runningMate.mainpage.fragments.running.ui;

import com.itba.runningMate.domain.Run;
import com.itba.runningMate.mainpage.fragments.running.model.Route;
import com.itba.runningMate.mainpage.fragments.running.repository.RunningStateStorage;
import com.itba.runningMate.mainpage.fragments.running.services.location.OnTrackingUpdateListener;
import com.itba.runningMate.mainpage.fragments.running.services.location.Tracker;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;
import java.util.Date;

import io.reactivex.disposables.Disposable;

public class RunningPresenter implements OnTrackingUpdateListener {

    private static final double DISTANCE_EPSILON = 0.1;
    private final WeakReference<RunningView> view;
    private final RunningStateStorage stateStorage;
    private final RunRepository runRepository;
    private final SchedulerProvider schedulers;

    private Tracker tracker;
    private boolean isTrackerAttached;
    private Disposable disposable;

    public RunningPresenter(final RunningStateStorage stateStorage,
                            final RunRepository runRepository,
                            final SchedulerProvider schedulers,
                            final RunningView view) {
        this.isTrackerAttached = false;
        this.view = new WeakReference<>(view);
        this.stateStorage = stateStorage;
        this.runRepository = runRepository;
        this.schedulers = schedulers;
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
        tracker.removeTrackingUpdateListener();
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
            Route route = tracker.queryRoute();
            if (!route.isEmpty()) {
                stateStorage.setLastKnownLocation(route.getLastLatitude(), route.getLastLongitude());
                view.get().showRoute(route);
                onPaceUpdate(tracker.queryPace());
                onDistanceUpdate(tracker.queryDistance());
                onStopWatchUpdate(tracker.queryElapsedTime());
                view.get().showStopRunButton();
            }
        }
    }

    public void onTrackingServiceDetached() {
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
        if (isTrackerAttached && tracker.isTracking()) {
            tracker.stopTracking();
            float distKm = tracker.queryDistance();
            long timeMillis = tracker.queryElapsedTime();
            Run run = new Run()
                    .startTime(new Date(tracker.queryStartTime()))
                    .elapsedTime(timeMillis)
                    .route(tracker.queryRoute().getLocations())
                    .distance(distKm)
                    .pace(tracker.queryPace())
                    .velocity(tracker.queryVelocity());
            saveRun(run);
        }
        if (view.get() != null) {
            view.get().removeRoutes();
            view.get().showInitialMetrics();
            view.get().showStartRunButton();
        }
    }

    private void saveRun(Run run) {
        if (run.getDistance() > 0F) {
            disposable = runRepository.insertRun(run)
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.ui())
                    .subscribe(this::onRunSaved, this::onRunSavedError);
        }
    }

    private void onRunSaved(final long runId) {
        if (view.get() == null) {
            return;
        }
        view.get().launchRunActivity(runId);
        disposable.dispose();
    }

    private void onRunSavedError(final Throwable e) {
        if (view.get() == null) {
            return;
        }
        view.get().showSaveRunError();
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

    public void onStartStopButtonClick() {
        if (view.get() == null || !isTrackerAttached) {
            return;
        }
        if (tracker.isTracking()) {
            if(tracker.queryDistance() < DISTANCE_EPSILON) {
                view.get().showStopConfirm();
            }else {
                stopRun();
            }
        } else {
            startRun();
            view.get().showStopRunButton();
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
        view.get().updateStopwatch(elapsedTime);
    }

    @Override
    public void onDistanceUpdate(float elapsedDistance) {
        if (view.get() == null) {
            return;
        }
        view.get().updateDistance(elapsedDistance);
    }

    @Override
    public void onPaceUpdate(long pace) {
        if (view.get() == null) {
            return;
        }
        view.get().updatePace(pace);
    }

}
