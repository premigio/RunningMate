package com.itba.runningMate.running.fragments.metrics;

import com.itba.runningMate.domain.Route;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.services.location.Tracker;
import com.itba.runningMate.utils.Constants;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;
import com.itba.runningMate.utils.run.RunMetrics;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static com.itba.runningMate.utils.Constants.DISTANCE_EPSILON;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RunningMetricsPresenterTest {

    private RunningStateStorage stateStorage;
    private RunRepository runRepository;
    private SchedulerProvider schedulers;
    private RunningMetricsView view;
    private Tracker tracker;

    private RunningMetricsPresenter presenter;
    private RunningMetricsPresenter presenterSpy;

    private Route route;

    @Before
    public void setUp() throws Exception {
        stateStorage = mock(RunningStateStorage.class);
        runRepository = mock(RunRepository.class);
        schedulers = mock(SchedulerProvider.class);
        view = mock(RunningMetricsView.class);
        tracker = mock(Tracker.class);

        presenter = new RunningMetricsPresenter(stateStorage, runRepository, schedulers, view);
        presenterSpy = spy(presenter);

        route = new Route().addToRoute(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE);
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

        presenter.onTrackingServiceAttached(tracker);
        presenter.onStopButtonClick();

        verify(view).showStopConfirm();
    }

    @Test
    public void givenPlayButtonClickThenResumeTracking() {
        when(tracker.isTracking()).thenReturn(false);

        presenter.onTrackingServiceAttached(tracker);
        presenter.onPlayButtonClick();

        verify(tracker).newLap();
        verify(tracker).resumeTracking();
    }

    @Test
    public void givenPlayButtonClickThenHidePlayButton() {
        when(tracker.isTracking()).thenReturn(false);

        presenter.onTrackingServiceAttached(tracker);
        presenter.onPlayButtonClick();

        verify(view).hidePlayBtn();
    }

    @Test
    public void givenPlayButtonClickThenHideStopButton() {
        when(tracker.isTracking()).thenReturn(false);

        presenter.onTrackingServiceAttached(tracker);
        presenter.onPlayButtonClick();

        verify(view).hideStopBtn();
    }

    @Test
    public void givenPlayButtonClickThenShowPauseButton() {
        when(tracker.isTracking()).thenReturn(false);

        presenter.onTrackingServiceAttached(tracker);
        presenter.onPlayButtonClick();

        verify(view).showPauseBtn();
    }


    @Test
    public void givenPauseButtonClickClickThenStopTracking() {
        when(tracker.isTracking()).thenReturn(true);

        presenter.onTrackingServiceAttached(tracker);
        presenter.onPauseButtonClick();

        verify(tracker).stopTracking();
    }


    @Test
    public void givenPauseButtonClickClickThenHidePauseButton() {
        when(tracker.isTracking()).thenReturn(true);

        presenter.onTrackingServiceAttached(tracker);
        presenter.onPauseButtonClick();

        verify(view).hidePauseBtn();
    }

    @Test
    public void givenPauseButtonClickClickThenShowPlayButton() {
        when(tracker.isTracking()).thenReturn(true);

        presenter.onTrackingServiceAttached(tracker);
        presenter.onPauseButtonClick();

        verify(view).showPlayBtn();
    }

    @Test
    public void givenPauseButtonClickClickThenHideStopButton() {
        when(tracker.isTracking()).thenReturn(true);

        presenter.onTrackingServiceAttached(tracker);
        presenter.onPauseButtonClick();

        verify(view).showStopBtn();
    }

    @Test
    public void givenTrackingServiceDetachedThenSetIsTrackerAttachedFalse() {
        presenter.onTrackingServiceDetached();

        assertFalse(presenter.isTrackerAttached());
    }

    @Test
    public void givenTrackingServiceDetachedThenSetTrackerNull() {
        presenter.onTrackingServiceDetached();

        assertNull(presenter.getTracker());
    }

    @Test
    public void givenTrackingServiceAttachedThenSetTrackingLocationUpdateListener() {
        presenter.onTrackingServiceAttached(tracker);

        verify(tracker).setTrackingMetricsUpdateListener(presenter);
    }

    @Test
    public void givenTrackingServiceAttachedThenShowTrackedMetrics() {
        long pace = 10L;
        float distance = 40F;
        long elapsedTime = 1000L;
        when(tracker.isTracking()).thenReturn(true);
        when(tracker.queryDistance()).thenReturn(distance);
        when(tracker.queryPace()).thenReturn(pace);
        when(tracker.queryElapsedTime()).thenReturn(elapsedTime);

        presenterSpy.onTrackingServiceAttached(tracker);


        verify(presenterSpy).onPaceUpdate(pace);
        verify(presenterSpy).onDistanceUpdate(distance);
        verify(presenterSpy).onStopWatchUpdate(elapsedTime);
    }

    @Test
    public void givenStopRunWhenTrackingThenStopTracking() {
        presenter.onTrackingServiceAttached(tracker);

        presenter.stopRun();

        verify(tracker).stopTracking();
    }

    @Test
    public void givenStopRunWhenTrackingDistanceLessThanThresholdThenEndActivity() {
        when(tracker.queryDistance()).thenReturn((float) (DISTANCE_EPSILON - 0.5));

        presenter.onTrackingServiceAttached(tracker);
        presenter.stopRun();

        verify(view).finishActivity();
    }

    @Test
    public void givenStopRunWhenTrackingDistanceGreaterThanThresholdThenSaveRun() {
        when(tracker.queryDistance()).thenReturn(10F);
        when(tracker.queryElapsedTime()).thenReturn(10000L);
        when(tracker.queryStartTime()).thenReturn(System.currentTimeMillis());
        when(tracker.queryEndTime()).thenReturn(System.currentTimeMillis() + 10000L);
        when(tracker.queryVelocity()).thenReturn(10F / 10000F);
        when(tracker.queryPace()).thenReturn(10000L / 10);
        when(tracker.queryRoute()).thenReturn(route);

        when(schedulers.io()).thenReturn(Schedulers.trampoline());
        when(schedulers.ui()).thenReturn(Schedulers.trampoline());

        long runId = 1L;
        when(runRepository.insertRun(any(Run.class))).thenReturn(Single.just(runId));

        presenter.onTrackingServiceAttached(tracker);
        presenter.stopRun();

        verify(view).launchRunActivity(runId);
    }

    @Test
    public void givenStopRunWhenTrackingDistanceGreaterThanThresholdAndErrorOnSaveRunThenShowErrorMessage() {
        when(tracker.queryDistance()).thenReturn(10F);
        when(tracker.queryElapsedTime()).thenReturn(10000L);
        when(tracker.queryStartTime()).thenReturn(System.currentTimeMillis());
        when(tracker.queryEndTime()).thenReturn(System.currentTimeMillis() + 10000L);
        when(tracker.queryVelocity()).thenReturn(10F / 10000F);
        when(tracker.queryPace()).thenReturn(10000L / 10);
        when(tracker.queryRoute()).thenReturn(route);

        when(schedulers.io()).thenReturn(Schedulers.trampoline());
        when(schedulers.ui()).thenReturn(Schedulers.trampoline());

        long runId = 1L;
        when(runRepository.insertRun(any(Run.class))).thenReturn(Single.error(new RuntimeException("Could not save run")));

        presenter.onTrackingServiceAttached(tracker);
        presenter.stopRun();

        verify(view).showSaveRunError();
    }

}