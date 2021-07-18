package com.itba.runningMate.pastruns

import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.achievements.AchievementsStorage
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.rundetails.model.RunMetricsDetail
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

class PastRunsPresenter(
    private val schedulerProvider: SchedulerProvider,
    private val runRepository: RunRepository,
    private val achievementsStorage: AchievementsStorage,
    view: PastRunsView
) {
    private val view: WeakReference<PastRunsView> = WeakReference(view)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun onViewAttached() {
        fetchRuns()
    }

    fun onViewDetached() {
        disposables.dispose()
    }

    fun fetchRuns() {
        disposables.add(runRepository.getRunLazy()
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ runs: List<Run>? -> receivedRunList(runs) }) { onRunListError() }
        )
    }

    private fun receivedRunList(runs: List<Run>?) {
        if (view.get() == null) {
            return
        }
        if (runs == null || runs.isEmpty()) {
            view.get()!!.showNoPastRunsMessage()
            view.get()!!.updatePastRuns(LinkedList())
        } else {
            view.get()!!.hideNoPastRunsMessage()
            view.get()!!.updatePastRuns(runs)
        }
    }

    private fun onRunListError() {
        Timber.d("Failed to retrieve runs from db")
        if (view.get() != null) {
            view.get()!!.showNoPastRunsMessage()
        }
    }

    fun onRunClick(id: Long) {
        if (view.get() != null) {
            view.get()!!.launchRunDetails(id)
        }
    }

    fun onSwipeRunToDelete(id: Long) {
        disposables.add(runRepository.deleteRun(id)
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ onRunDeleted(id) }) { onRunDeleteError(id) })
        disposables.add(runRepository.getRunMetrics(id)
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ run: Run -> onReceivedRunMetrics(run) }) { onReceivedRunMetricsError() })
    }

    private fun onReceivedRunMetrics(run: Run) {
        val distance = run.distance!!.toDouble()
        achievementsStorage.decreaseTotalDistance(distance)
        achievementsStorage.persistState()
    }

    private fun onReceivedRunMetricsError() {
        Timber.d("Failed to decrease total distance when run deleted")
    }

    private fun onRunDeleted(id: Long) {
        Timber.i("Successfully deleted run from db for run-id: %d", id)
    }

    private fun onRunDeleteError(id: Long) {
        Timber.d("Failed to delete run from db for run-id: %d", id)
        if (view.get() != null) {
            view.get()!!.showDeleteError()
        }
    }
}