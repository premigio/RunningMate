package com.itba.runningMate.mainpage.fragments.running.ui;

import android.annotation.SuppressLint;

import com.itba.runningMate.domain.Sprint;
import com.itba.runningMate.mainpage.fragments.running.model.Route;
import com.itba.runningMate.mainpage.fragments.running.repository.LandingStateStorage;
import com.itba.runningMate.mainpage.fragments.running.services.location.OnTrackingUpdateListener;
import com.itba.runningMate.mainpage.fragments.running.services.location.Tracker;
import com.itba.runningMate.repository.sprint.SprintRepository;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;

public class RunningPresenter implements OnTrackingUpdateListener {

    private static final DecimalFormat twoDecimalPlacesFormatter = new DecimalFormat("0.00");

    private final WeakReference<RunningView> view;
    private final LandingStateStorage stateStorage;
    private final SprintRepository sprintRepository;
    private SchedulerProvider schedulers;

    private Tracker tracker;
    private boolean isTrackerAttached;
    private Disposable disposable;

    public RunningPresenter(final LandingStateStorage stateStorage,
                            final SprintRepository sprintRepository,
                            final SchedulerProvider schedulers,
                            final RunningView view) {
        this.isTrackerAttached = false;
        this.view = new WeakReference<>(view);
        this.stateStorage = stateStorage;
        this.sprintRepository = sprintRepository;
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
                view.get().showStopSprintButton();
            }
        }
    }

    public void onTrackingServiceDetached() {
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
        if (isTrackerAttached && tracker.isTracking()) {
            tracker.stopTracking();
            float distKm = tracker.queryDistance();
            long timeMillis = tracker.queryElapsedTime();
            Sprint sprint = new Sprint()
                    .startTime(new Date(tracker.queryStartTime()))
                    .elapsedTime(timeMillis)
                    .route(tracker.querySprint().getLocations())
                    .distance(distKm)
                    .pace(tracker.queryPace())
                    .velocity(tracker.queryVelocity());
            saveSprint(sprint);
        }
        if (view.get() != null) {
            view.get().removeRoutes();
            view.get().showInitialMetrics();
        }
    }

    private void saveSprint(Sprint sprint) {
        if (sprint.getDistance() > 0F) {
            disposable = sprintRepository.insertSprint(sprint)
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.ui())
                    .subscribe(this::onSprintSaved, this::onSprintSavedError);
        }
    }

    private void onSprintSaved(final long sprintId) {
        if (view.get() == null) {
            return;
        }
        view.get().launchSprintActivity(sprintId);
        disposable.dispose();
    }

    private void onSprintSavedError(final Throwable e) {
        if (view.get() == null) {
            return;
        }
        view.get().showSaveSprintError();
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
            stopRun();
            view.get().showStartSprintButton();
        } else {
            startRun();
            view.get().showStopSprintButton();
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
        view.get().updateStopwatch(hmsTimeFormatter(elapsedTime));
    }

    @Override
    public void onDistanceUpdate(float elapsedDistance) {
        if (view.get() == null) {
            return;
        }
        view.get().updateDistance(twoDecimalPlacesFormatter.format(elapsedDistance));
    }

    @Override
    public void onPaceUpdate(long pace) {
        if (view.get() == null) {
            return;
        }
        view.get().updatePace(hmsTimeFormatter(pace));
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
