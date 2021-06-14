package com.itba.runningMate.running.fragments.metrics;

import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.services.location.Tracker;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;
import com.itba.runningMate.utils.run.RunMetrics;

import org.junit.Before;
import org.junit.Test;

import static com.itba.runningMate.utils.Constants.DISTANCE_EPSILON;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RunningMetricsPresenterTest {

    private RunningStateStorage stateStorage;
    private RunRepository runRepository;
    private SchedulerProvider schedulers;
    private RunningMetricsView view;
    private Tracker tracker;

    private RunningMetricsPresenter presenter;

    @Before
    public void setUp() throws Exception {
        stateStorage = mock(RunningStateStorage.class);
        runRepository = mock(RunRepository.class);
        schedulers = mock(SchedulerProvider.class);
        view = mock(RunningMetricsView.class);
        tracker = mock(Tracker.class);

        presenter = new RunningMetricsPresenter(stateStorage, runRepository, schedulers, view);
        presenter.onTrackingServiceAttached(tracker);
    }

    @Test
    public void givenViewAttachedThenAttachTrackingService() {
        presenter.onViewAttached();

        verify(view).attachTrackingService();
    }

    @Test
    public void givenViewDetachedThenPersistStateStorage() {
        presenter.onViewDetached();

        verify(stateStorage).persistState();
    }

    @Test
    public void givenViewDetachedThenDetachTrackingService() {
        presenter.onViewDetached();

        verify(view).detachTrackingService();
    }

//    public void stopRun() {
//        if (!isTrackerAttached) {
//            return;
//        }
//        tracker.stopTracking();
//        float distKm = tracker.queryDistance();
//        if (tracker.queryDistance() < DISTANCE_EPSILON && view.get() != null) {
//            view.get().finishActivity();
//        } else {
//            long timeMillis = tracker.queryElapsedTime();
//            Run run = new Run()
//                    .title("Run on ".concat(dateFormat.format(new Date(tracker.queryStartTime()))))
//                    .startTime(new Date(tracker.queryStartTime()))
//                    .endTime(new Date(tracker.queryEndTime()))
//                    .runningTime(timeMillis)
//                    .route(tracker.queryRoute().getLocations())
//                    .distance(distKm)
//                    .pace(tracker.queryPace())
//                    .velocity(tracker.queryVelocity())
//                    .calories(RunMetrics.calculateCalories(distKm));
//            saveRun(run);
//        }
//    }

    @Test
    public void givenStopWatchUpdateThenShowStopWatchUpdate() {
        long elapsedTime = 100L;

        presenter.onStopWatchUpdate(elapsedTime);

        verify(view).updateStopwatch(elapsedTime);
    }

    @Test
    public void givenDistanceUpdateThenShowUpdateDistance() {
        float elapsedDistance = 100L;

        presenter.onDistanceUpdate(elapsedDistance);

        verify(view).updateDistance(elapsedDistance);
    }

    @Test
    public void givenDistanceUpdateThenShowUpdateCalories() {
        float elapsedDistance = 100L;

        presenter.onDistanceUpdate(elapsedDistance);

        verify(view).updateCalories(RunMetrics.calculateCalories(elapsedDistance));
    }

    @Test
    public void givenPaceUpdateThenShowPaceUpdate() {
        long pace = 100L;

        presenter.onPaceUpdate(pace);

        verify(view).updatePace(pace);
    }

    @Test
    public void givenStopButtonClickWhenDistanceLessThanThresholdTHenShowStopConfirm() {
        when(tracker.queryDistance()).thenReturn((float) (DISTANCE_EPSILON - 0.5));

        presenter.onStopButtonClick();

        verify(view).showStopConfirm();
    }

    @Test
    public void givenPlayButtonClickThenResumeTracking() {
        when(tracker.isTracking()).thenReturn(false);

        presenter.onPlayButtonClick();

        verify(tracker).newLap();
        verify(tracker).resumeTracking();
    }

    @Test
    public void givenPlayButtonClickThenHidePlayButton() {
        when(tracker.isTracking()).thenReturn(false);

        presenter.onPlayButtonClick();

        verify(view).hidePlayBtn();
    }

    @Test
    public void givenPlayButtonClickThenHideStopButton() {
        when(tracker.isTracking()).thenReturn(false);

        presenter.onPlayButtonClick();

        verify(view).hideStopBtn();
    }

    @Test
    public void givenPlayButtonClickThenShowPauseButton() {
        when(tracker.isTracking()).thenReturn(false);

        presenter.onPlayButtonClick();

        verify(view).showPauseBtn();
    }


    @Test
    public void givenPauseButtonClickClickThenStopTracking() {
        when(tracker.isTracking()).thenReturn(true);

        presenter.onPauseButtonClick();

        verify(tracker).stopTracking();
    }


    @Test
    public void givenPauseButtonClickClickThenHidePauseButton() {
        when(tracker.isTracking()).thenReturn(true);

        presenter.onPauseButtonClick();

        verify(view).hidePauseBtn();
    }

    @Test
    public void givenPauseButtonClickClickThenShowPlayButton() {
        when(tracker.isTracking()).thenReturn(true);

        presenter.onPauseButtonClick();

        verify(view).showPlayBtn();
    }

    @Test
    public void givenPauseButtonClickClickThenHideStopButton() {
        when(tracker.isTracking()).thenReturn(true);

        presenter.onPauseButtonClick();

        verify(view).showStopBtn();
    }

}