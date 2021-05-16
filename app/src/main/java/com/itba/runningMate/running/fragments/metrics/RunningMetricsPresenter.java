package com.itba.runningMate.running.fragments.metrics;

import com.itba.runningMate.domain.Run;
import com.itba.runningMate.mainpage.fragments.running.repository.RunningStateStorage;
import com.itba.runningMate.mainpage.fragments.running.services.location.OnTrackingMetricsUpdateListener;
import com.itba.runningMate.mainpage.fragments.running.services.location.OnTrackingUpdateListener;
import com.itba.runningMate.mainpage.fragments.running.services.location.Tracker;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;
import java.util.Date;

import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class RunningMetricsPresenter implements OnTrackingMetricsUpdateListener {

    private static final double DISTANCE_EPSILON = 0.1;
    private final WeakReference<RunningMetricsView> view;
    private final RunningStateStorage stateStorage;
    private final RunRepository runRepository;
    private final SchedulerProvider schedulers;

    private Tracker tracker;
    private boolean isTrackerAttached;
    private Disposable disposable;

    public RunningMetricsPresenter(final RunningStateStorage stateStorage,
                                   final RunRepository runRepository,
                                   final SchedulerProvider schedulers,
                                   final RunningMetricsView view) {
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
        view.get().attachTrackingService();
    }

    public void onViewDetached() {
        stateStorage.persistState();
        tracker.removeTrackingMetricsUpdateListener(this);
        if (view.get() != null) {
            view.get().detachTrackingService();
        }
    }

    public void onTrackingServiceAttached(Tracker tracker) {
        this.tracker = tracker;
        this.isTrackerAttached = true;
        tracker.setTrackingMetricsUpdateListener(this);
        if (tracker.isTracking() && view.get() != null) {
            onPaceUpdate(tracker.queryPace());
            onDistanceUpdate(tracker.queryDistance());
            onStopWatchUpdate(tracker.queryElapsedTime());
        }
    }

    public void onTrackingServiceDetached() {
        this.tracker = null;
        this.isTrackerAttached = false;
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
            view.get().showInitialMetrics();
        }
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

    public void onStartStopButtonClick() {
        if (view.get() == null || !isTrackerAttached) {
            return;
        }
        if (tracker.isTracking()) {
            if (tracker.queryDistance() < DISTANCE_EPSILON) {
                view.get().showStopConfirm();
            } else {
                stopRun();
            }
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
        Timber.d("Successfully saved run in db for run-id: %d", runId);
    }

    private void onRunSavedError(final Throwable e) {
        if (view.get() == null) {
            return;
        }
        Timber.d("Failed to save run in");
        view.get().showSaveRunError();
    }
}
