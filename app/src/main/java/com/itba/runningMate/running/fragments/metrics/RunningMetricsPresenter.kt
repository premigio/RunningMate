package com.itba.runningMate.running.fragments.metrics

import androidx.annotation.VisibleForTesting
import com.itba.runningMate.domain.Achievements
import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.achievements.AchievementsRepository
import com.itba.runningMate.repository.aggregaterunmetrics.AggregateRunMetricsStorage
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.services.location.Tracker
import com.itba.runningMate.services.location.listeners.OnTrackingMetricsUpdateListener
import com.itba.runningMate.utils.Constants.DISTANCE_EPSILON
import com.itba.runningMate.utils.Formatters
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import com.itba.runningMate.utils.run.RunMetrics.calculateCalories
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

class RunningMetricsPresenter(
    private val runRepository: RunRepository,
    private val schedulers: SchedulerProvider,
    private val achievementsRepository: AchievementsRepository,
    private val aggregateRunMetricsStorage: AggregateRunMetricsStorage,
    view: RunningMetricsView?
) : OnTrackingMetricsUpdateListener {

    private val view: WeakReference<RunningMetricsView> = WeakReference(view)

    private var disposable: Disposable? = null

    @get:VisibleForTesting
    var tracker: Tracker? = null
        private set

    @get:VisibleForTesting
    var isTrackerAttached = false
        private set


    fun onViewAttached() {
        if (view.get() == null) {
            return
        }
        view.get()!!.attachTrackingService()
    }

    fun onViewDetached() {
        if (isTrackerAttached) {
            tracker!!.removeTrackingMetricsUpdateListener(this)
        }
        if (view.get() != null) {
            view.get()!!.detachTrackingService()
        }
        if (disposable != null) {
            disposable!!.dispose()
        }
    }

    fun onTrackingServiceAttached(tracker: Tracker) {
        this.tracker = tracker
        isTrackerAttached = true
        tracker.setTrackingMetricsUpdateListener(this)
        if (tracker.isTracking() && view.get() != null) {
            onPaceUpdate(tracker.queryPace())
            onDistanceUpdate(tracker.queryDistance())
            onStopWatchUpdate(tracker.queryElapsedTime())
        }
    }

    fun onTrackingServiceDetached() {
        tracker = null
        isTrackerAttached = false
    }

    fun stopRun() {
        if (!isTrackerAttached) {
            return
        }
        tracker!!.stopTracking()
        val distKm = tracker!!.queryDistance()
        if (tracker!!.queryDistance() < DISTANCE_EPSILON && view.get() != null) {
            view.get()!!.finishActivity()
        } else {
            val timeMillis = tracker!!.queryElapsedTime()
            val run = Run.Builder()
                .title("Run on " + Formatters.dateFormat.format(Date(tracker!!.queryStartTime())))
                .startTime(Date(tracker!!.queryStartTime()))
                .endTime(Date(tracker!!.queryEndTime()))
                .runningTime(timeMillis)
                .route(tracker!!.queryRoute().getLocations())
                .distance(distKm)
                .pace(tracker!!.queryPace())
                .velocity(tracker!!.queryVelocity())
                .calories(calculateCalories(distKm))
                .build()
            saveRun(run)
            updateAggregateMetrics(run)
        }
    }

    override fun onStopWatchUpdate(elapsedTime: Long) {
        if (view.get() == null) {
            return
        }
        view.get()!!.updateStopwatch(elapsedTime)
    }

    override fun onDistanceUpdate(elapsedDistance: Float) {
        if (view.get() == null) {
            return
        }
        view.get()!!.updateDistance(elapsedDistance)
        view.get()!!.updateCalories(calculateCalories(elapsedDistance))
    }

    override fun onPaceUpdate(pace: Long) {
        if (view.get() == null) {
            return
        }
        view.get()!!.updatePace(pace)
    }

    fun onStopButtonClick() {
        if (view.get() == null || !isTrackerAttached) {
            return
        }
        if (tracker!!.queryDistance() < DISTANCE_EPSILON) {
            view.get()!!.showStopConfirm()
        } else {
            stopRun()
        }
    }

    fun onPlayButtonClick() {
        if (view.get() == null || !isTrackerAttached) {
            return
        }
        if (!tracker!!.isTracking()) {
            tracker!!.newLap()
            tracker!!.resumeTracking()
            view.get()!!.hidePlayBtn()
            view.get()!!.hideStopBtn()
            view.get()!!.showPauseBtn()
        }
    }

    fun onPauseButtonClick() {
        if (view.get() == null || !isTrackerAttached) {
            return
        }
        if (tracker!!.isTracking()) {
            tracker!!.stopTracking()
            view.get()!!.hidePauseBtn()
            view.get()!!.showPlayBtn()
            view.get()!!.showStopBtn()
        }
    }

    private fun saveRun(run: Run) {
        if (run.distance!! > 0f) {
            disposable = runRepository.insertRun(run)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe({ runId: Long -> onRunSaved(runId) }) { onRunSavedError() }
        }
    }

    private fun updateAggregateMetrics(run: Run) {
        aggregateRunMetricsStorage.incrementTotalDistance(run.distance!!.toDouble())
        aggregateRunMetricsStorage.updateMaxDistance(run.distance.toDouble())
        aggregateRunMetricsStorage.updateMaxRunningTime(run.runningTime!!)
        aggregateRunMetricsStorage.updateMaxSpeed(run.velocity!!)
        aggregateRunMetricsStorage.updateMaxPace(run.pace!!)
        aggregateRunMetricsStorage.updateMaxCalories(run.calories!!)
        aggregateRunMetricsStorage.persistState()
        computeAchievements(run.startTime!!)
    }

    private fun computeAchievements(timestamp: Date) {
        val aggregate = aggregateRunMetricsStorage.getAggregateRunMetricsDetail()
        val completedAchievements: MutableList<Achievements> = mutableListOf()
        for (a in Achievements.values()) {
            if (a.completed(aggregate)) {
                completedAchievements.add(a)
            }
        }
        achievementsRepository.addAchievements(completedAchievements, timestamp)
            .subscribeOn(schedulers.computation())
            .subscribe()
    }

    private fun onRunSaved(runId: Long) {
        if (view.get() == null) {
            return
        }
        view.get()!!.launchRunActivity(runId)
        Timber.d("Successfully saved run in db for run-id: %d", runId)
    }

    private fun onRunSavedError() {
        Timber.d("Failed to save run on Db")
    }

}