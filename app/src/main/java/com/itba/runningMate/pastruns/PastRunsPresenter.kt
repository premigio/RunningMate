package com.itba.runningMate.pastruns

import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

class PastRunsPresenter(
    private val scope: CoroutineScope,
    private val runRepository: RunRepository,
    view: PastRunsView
) {
    private val view: WeakReference<PastRunsView> = WeakReference(view)

    fun onViewAttached() {
        scope.launch {
            launch {
                runRepository.getRunLazy().collect{r ->
                    withContext(Dispatchers.Main){
                        receivedRunList(r.filterNotNull())
                    }
                }
            }
        }
    }

    private fun onRunListError(err: Throwable) {
        Timber.d("Failed to retrieve runs from db")
        if (view.get() != null) {
            view.get()!!.showNoPastRunsMessage()
        }
    }

    fun onViewDetached() {
        scope.cancel()
    }

    private fun receivedRunList(runs: List<Run?>?) {
        if (view.get() == null) {
            return
        }
        if (runs == null || runs.isEmpty()) {
            view.get()!!.showNoPastRunsMessage()
            view.get()!!.updatePastRuns(LinkedList())
        } else {
            view.get()!!.hideNoPastRunsMessage()
            view.get()!!.updatePastRuns(runs.filterNotNull())
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