package com.itba.runningMate.running.fragments.metrics

import com.google.firebase.messaging.FirebaseMessaging
import com.itba.runningMate.domain.AggregateRunMetricsDetail
import com.itba.runningMate.domain.Route
import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.achievements.AchievementsRepository
import com.itba.runningMate.repository.aggregaterunmetrics.AggregateRunMetricsStorage
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.services.location.Tracker
import com.itba.runningMate.utils.Constants
import com.itba.runningMate.utils.Constants.DISTANCE_EPSILON
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import com.itba.runningMate.utils.run.RunMetrics.calculateCalories
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor

class RunningMetricsPresenterTest {

    private lateinit var firebaseMessaging: FirebaseMessaging
    private lateinit var aggregateRunMetricsStorage: AggregateRunMetricsStorage
    private lateinit var achievementsRepository: AchievementsRepository
    private lateinit var runRepository: RunRepository
    private lateinit var schedulers: SchedulerProvider
    private lateinit var view: RunningMetricsView
    private lateinit var tracker: Tracker
    private lateinit var presenter: RunningMetricsPresenter
    private lateinit var presenterSpy: RunningMetricsPresenter
    private lateinit var route: Route

    private var aggregateMetrics: AggregateRunMetricsDetail = AggregateRunMetricsDetail.Builder()
        .distance(50f)
        .calories(40)
        .pace(40)
        .runningTime(40)
        .elapsedTime(40)
        .speed(30f)
        .build()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        firebaseMessaging = Mockito.mock(FirebaseMessaging::class.java)
        aggregateRunMetricsStorage = Mockito.mock(AggregateRunMetricsStorage::class.java)
        achievementsRepository = Mockito.mock(AchievementsRepository::class.java)
        runRepository = Mockito.mock(RunRepository::class.java)
        schedulers = Mockito.mock(SchedulerProvider::class.java)
        view = Mockito.mock(RunningMetricsView::class.java)
        tracker = Mockito.mock(Tracker::class.java)
        presenter = RunningMetricsPresenter(
            runRepository,
            schedulers,
            achievementsRepository,
            aggregateRunMetricsStorage,
            firebaseMessaging,
            view
        )
        presenterSpy = Mockito.spy(presenter)
        route = Route().addToRoute(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE)
    }

    @Test
    fun givenViewAttachedThenAttachTrackingService() {
        presenter.onViewAttached()
        Mockito.verify(view).attachTrackingService()
    }

    @Test
    fun givenViewDetachedThenDetachTrackingService() {
        presenter.onViewDetached()
        Mockito.verify(view).detachTrackingService()
    }

    @Test
    fun givenStopWatchUpdateThenShowStopWatchUpdate() {
        val elapsedTime = 100L
        presenter.onStopWatchUpdate(elapsedTime)
        Mockito.verify(view).updateStopwatch(elapsedTime)
    }

    @Test
    fun givenDistanceUpdateThenShowUpdateDistance() {
        val elapsedDistance = 100f
        presenter.onDistanceUpdate(elapsedDistance)
        Mockito.verify(view).updateDistance(elapsedDistance)
    }

    @Test
    fun givenDistanceUpdateThenShowUpdateCalories() {
        val elapsedDistance = 100f
        presenter.onDistanceUpdate(elapsedDistance)
        Mockito.verify(view).updateCalories(calculateCalories(elapsedDistance))
    }

    @Test
    fun givenPaceUpdateThenShowPaceUpdate() {
        val pace = 100L
        presenter.onPaceUpdate(pace)
        Mockito.verify(view).updatePace(pace)
    }

    @Test
    fun givenStopButtonClickWhenDistanceLessThanThresholdTHenShowStopConfirm() {
        Mockito.`when`(tracker.queryDistance()).thenReturn((DISTANCE_EPSILON - 0.5).toFloat())
        presenter.onTrackingServiceAttached(tracker)
        presenter.onStopButtonClick()
        Mockito.verify(view).showStopConfirm()
    }

    @Test
    fun givenPlayButtonClickThenResumeTracking() {
        Mockito.`when`(tracker.isTracking()).thenReturn(false)
        presenter.onTrackingServiceAttached(tracker)
        presenter.onPlayButtonClick()
        Mockito.verify(tracker).newLap()
        Mockito.verify(tracker).resumeTracking()
    }

    @Test
    fun givenPlayButtonClickThenHidePlayButton() {
        Mockito.`when`(tracker.isTracking()).thenReturn(false)
        presenter.onTrackingServiceAttached(tracker)
        presenter.onPlayButtonClick()
        Mockito.verify(view).hidePlayBtn()
    }

    @Test
    fun givenPlayButtonClickThenHideStopButton() {
        Mockito.`when`(tracker.isTracking()).thenReturn(false)
        presenter.onTrackingServiceAttached(tracker)
        presenter.onPlayButtonClick()
        Mockito.verify(view).hideStopBtn()
    }

    @Test
    fun givenPlayButtonClickThenShowPauseButton() {
        Mockito.`when`(tracker.isTracking()).thenReturn(false)
        presenter.onTrackingServiceAttached(tracker)
        presenter.onPlayButtonClick()
        Mockito.verify(view).showPauseBtn()
    }

    @Test
    fun givenPauseButtonClickClickThenStopTracking() {
        Mockito.`when`(tracker.isTracking()).thenReturn(true)
        presenter.onTrackingServiceAttached(tracker)
        presenter.onPauseButtonClick()
        Mockito.verify(tracker).stopTracking()
    }

    @Test
    fun givenPauseButtonClickClickThenHidePauseButton() {
        Mockito.`when`(tracker.isTracking()).thenReturn(true)
        presenter.onTrackingServiceAttached(tracker)
        presenter.onPauseButtonClick()
        Mockito.verify(view).hidePauseBtn()
    }

    @Test
    fun givenPauseButtonClickClickThenShowPlayButton() {
        Mockito.`when`(tracker.isTracking()).thenReturn(true)
        presenter.onTrackingServiceAttached(tracker)
        presenter.onPauseButtonClick()
        Mockito.verify(view).showPlayBtn()
    }

    @Test
    fun givenPauseButtonClickClickThenHideStopButton() {
        Mockito.`when`(tracker.isTracking()).thenReturn(true)
        presenter.onTrackingServiceAttached(tracker)
        presenter.onPauseButtonClick()
        Mockito.verify(view).showStopBtn()
    }

    @Test
    fun givenTrackingServiceDetachedThenSetIsTrackerAttachedFalse() {
        presenter.onTrackingServiceDetached()
        Assert.assertFalse(presenter.isTrackerAttached)
    }

    @Test
    fun givenTrackingServiceDetachedThenSetTrackerNull() {
        presenter.onTrackingServiceDetached()
        Assert.assertNull(presenter.tracker)
    }

    @Test
    fun givenTrackingServiceAttachedThenSetTrackingLocationUpdateListener() {
        presenter.onTrackingServiceAttached(tracker)
        Mockito.verify(tracker).setTrackingMetricsUpdateListener(presenter)
    }

    @Test
    fun givenTrackingServiceAttachedThenShowTrackedMetrics() {
        val pace = 10L
        val distance = 40f
        val elapsedTime = 1000L
        Mockito.`when`(tracker.isTracking()).thenReturn(true)
        Mockito.`when`(tracker.queryDistance()).thenReturn(distance)
        Mockito.`when`(tracker.queryPace()).thenReturn(pace)
        Mockito.`when`(tracker.queryElapsedTime()).thenReturn(elapsedTime)
        presenterSpy.onTrackingServiceAttached(tracker)
        Mockito.verify(presenterSpy).onPaceUpdate(pace)
        Mockito.verify(presenterSpy).onDistanceUpdate(distance)
        Mockito.verify(presenterSpy).onStopWatchUpdate(elapsedTime)
    }

    @Test
    fun givenStopRunWhenTrackingThenStopTracking() {
        presenter.onTrackingServiceAttached(tracker)
        presenter.stopRun()
        Mockito.verify(tracker).stopTracking()
    }

    @Test
    fun givenStopRunWhenTrackingDistanceLessThanThresholdThenEndActivity() {
        Mockito.`when`(tracker.queryDistance()).thenReturn((DISTANCE_EPSILON - 0.5).toFloat())
        presenter.onTrackingServiceAttached(tracker)
        presenter.stopRun()
        Mockito.verify(view).finishActivity()
    }

    @Test
    fun givenStopRunWhenTrackingDistanceGreaterThanThresholdThenSaveRun() {
        Mockito.`when`(tracker.queryDistance()).thenReturn(10f)
        Mockito.`when`(tracker.queryElapsedTime()).thenReturn(10000L)
        Mockito.`when`(tracker.queryStartTime()).thenReturn(System.currentTimeMillis())
        Mockito.`when`(tracker.queryEndTime()).thenReturn(System.currentTimeMillis() + 10000L)
        Mockito.`when`(tracker.queryVelocity()).thenReturn(10f / 10000f)
        Mockito.`when`(tracker.queryPace()).thenReturn(10000L / 10)
        Mockito.`when`(tracker.queryRoute()).thenReturn(route)
        Mockito.`when`(aggregateRunMetricsStorage.getAggregateRunMetricsDetail())
            .thenReturn(aggregateMetrics)

        Mockito.`when`(schedulers.io()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(schedulers.ui()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(schedulers.computation()).thenReturn(Schedulers.trampoline())

        val runId = 1L
        val argumentCaptor = argumentCaptor<Run>()

        Mockito.`when`(
            runRepository.insertRun(argumentCaptor.capture())
        ).thenReturn(Single.just(runId))

        Mockito.`when`(
            achievementsRepository.addAchievements(any(), any())
        ).thenReturn(Completable.complete())

        presenter.onTrackingServiceAttached(tracker)
        presenter.stopRun()
        Mockito.verify(view).launchRunActivity(runId)
    }

    @Test
    fun givenStopRunWhenTrackingDistanceGreaterThanThresholdAndErrorOnSaveRunThenShowErrorMessage() {
        Mockito.`when`(tracker.queryDistance()).thenReturn(10f)
        Mockito.`when`(tracker.queryElapsedTime()).thenReturn(10000L)
        Mockito.`when`(tracker.queryStartTime()).thenReturn(System.currentTimeMillis())
        Mockito.`when`(tracker.queryEndTime()).thenReturn(System.currentTimeMillis() + 10000L)
        Mockito.`when`(tracker.queryVelocity()).thenReturn(10f / 10000f)
        Mockito.`when`(tracker.queryPace()).thenReturn(10000L / 10)
        Mockito.`when`(tracker.queryRoute()).thenReturn(route)
        Mockito.`when`(schedulers.io()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(schedulers.ui()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(schedulers.computation()).thenReturn(Schedulers.trampoline())

        Mockito.`when`(aggregateRunMetricsStorage.getAggregateRunMetricsDetail())
            .thenReturn(aggregateMetrics)

        Mockito.`when`(
            achievementsRepository.addAchievements(any(), any())
        ).thenReturn(Completable.complete())

        Mockito.`when`(
            runRepository.insertRun(any())
        ).thenReturn(Single.error(RuntimeException()))

        presenter.onTrackingServiceAttached(tracker)
        presenter.stopRun()

        Mockito.verify(view).showSaveRunError()
    }
}