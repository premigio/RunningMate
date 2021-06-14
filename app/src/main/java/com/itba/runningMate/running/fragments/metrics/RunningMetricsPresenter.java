package com.itba.runningMate.running.fragments.metrics;

import androidx.annotation.VisibleForTesting;

import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.services.location.listeners.OnTrackingMetricsUpdateListener;
import com.itba.runningMate.services.location.Tracker;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.run.RunMetrics;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;
import java.util.Date;

import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static com.itba.runningMate.utils.Constants.DISTANCE_EPSILON;
import static com.itba.runningMate.utils.Formatters.dateFormat;

public class RunningMetricsPresenter implements OnTrackingMetricsUpdateListener {

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
        if (isTrackerAttached) {
            tracker.removeTrackingMetricsUpdateListener(this);
        }
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
        if (!isTrackerAttached) {
            return;
        }
        tracker.stopTracking();
        float distKm = tracker.queryDistance();
        if (tracker.queryDistance() < DISTANCE_EPSILON && view.get() != null) {
            view.get().finishActivity();
        } else {
            long timeMillis = tracker.queryElapsedTime();
            Run run = new Run()
                    .title("Run on ".concat(dateFormat.format(new Date(tracker.queryStartTime()))))
                    .startTime(new Date(tracker.queryStartTime()))
                    .endTime(new Date(tracker.queryEndTime()))
                    .runningTime(timeMillis)
                    .route(tracker.queryRoute().getLocations())
                    .distance(distKm)
                    .pace(tracker.queryPace())
                    .velocity(tracker.queryVelocity())
                    .calories(RunMetrics.calculateCalories(distKm));
            saveRun(run);
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
        view.get().updateCalories(RunMetrics.calculateCalories(elapsedDistance));
    }

    @Override
    public void onPaceUpdate(long pace) {
        if (view.get() == null) {
            return;
        }
        view.get().updatePace(pace);
    }

    public void onStopButtonClick() {
        if (view.get() == null || !isTrackerAttached) {
            return;
        }
        if (tracker.queryDistance() < DISTANCE_EPSILON) {
            view.get().showStopConfirm();
        } else {
            stopRun();
        }
    }

    public void onPlayButtonClick() {
        if (view.get() == null || !isTrackerAttached) {
            return;
        }
        if (!tracker.isTracking()) {
            tracker.newLap();
            tracker.resumeTracking();
            view.get().hidePlayBtn();
            view.get().hideStopBtn();
            view.get().showPauseBtn();
        }
    }

    public void onPauseButtonClick() {
        if (view.get() == null || !isTrackerAttached) {
            return;
        }
        if (tracker.isTracking()) {
            tracker.stopTracking();
            view.get().hidePauseBtn();
            view.get().showPlayBtn();
            view.get().showStopBtn();
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
        Timber.d("Failed to save run\n".concat(e.getMessage()));
        view.get().showSaveRunError();
    }

    @VisibleForTesting
    public Tracker getTracker() {
        return tracker;
    }

    @VisibleForTesting
    public boolean isTrackerAttached() {
        return isTrackerAttached;
    }
}
