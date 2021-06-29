package com.itba.runningMate.pastruns

import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

class PastRunsPresenter(
    private val schedulerProvider: SchedulerProvider,
    private val runRepository: RunRepository,
    view: PastRunsView
) {
    private val view: WeakReference<PastRunsView> = WeakReference(view)
    private var disposable: Disposable? = null

    fun onViewAttached() {
        disposable = runRepository.getRunLazy()
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ runs: List<Run>? -> receivedRunList(runs) }) { err: Throwable ->
                onRunListError(
                    err
                )
            }
    }

    private fun onRunListError(err: Throwable) {
        Timber.d("Failed to retrieve runs from db")
        if (view.get() != null) {
            view.get()!!.showNoPastRunsMessage()
        }
    }

    fun onViewDetached() {
        disposable!!.dispose()
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

    fun refreshAction() {
        onViewAttached()
    }

    fun onRunClick(id: Long) {
        if (view.get() != null) {
            view.get()!!.launchRunDetails(id)
        }
    }
}