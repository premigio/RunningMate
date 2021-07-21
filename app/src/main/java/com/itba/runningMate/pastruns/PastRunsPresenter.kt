package com.itba.runningMate.pastruns

import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.aggregaterunmetrics.AggregateRunMetricsStorage
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

class PastRunsPresenter(
    private val schedulerProvider: SchedulerProvider,
    private val runRepository: RunRepository,
    private val aggregateRunMetricsStorage: AggregateRunMetricsStorage,
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