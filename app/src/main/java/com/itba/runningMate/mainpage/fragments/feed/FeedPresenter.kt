package com.itba.runningMate.mainpage.fragments.feed

import com.itba.runningMate.R
import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.run.RunRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.lang.ref.WeakReference

class FeedPresenter(
    private val repo: RunRepository,
    private val scope: CoroutineScope,
    view: FeedView
) {

    private val view: WeakReference<FeedView> = WeakReference(view)

    fun onViewAttached() {
        scope.launch {
            launch {
                repo.getRunLazy().collect{r ->
                    withContext(Dispatchers.Main){
                        receivedRunList(r.filterNotNull())
                    }
                }
            }
            launch {
                withContext(Dispatchers.Main){
                    goalLevel()
                }
            }
        }

    }

    fun onViewDetached() {
        scope.cancel()
    }

    private fun onRunListError(throwable: Throwable) {
        Timber.d("Failed to retrieve runs from db")
        if (view.get() != null) {
            view.get()!!.setPastRunCardsNoText()
        }
    }

    private fun receivedRunList(runs: List<Run>) {
        Timber.i("Runs %d", runs.size)
        if (view.get() != null) {
            if (runs.isEmpty()) {
                view.get()!!.setPastRunCardsNoText()
                view.get()!!.disappearRuns(0)
                return
            }
            view.get()!!.disappearNoText()
            val maxVal = runs.size.coerceAtMost(3)
            for (i in 1..maxVal) {
                //add data to view
                view.get()!!.addRunToCard(i - 1, runs[i - 1])
            }
            // disappear the run cards where they should not be
            view.get()!!.disappearRuns(maxVal)
        }
    }

    fun onPastRunClick(id: Long) {
        if (view.get() != null) {
            view.get()!!.launchRunDetailActivity(id)
        }
    }

    fun goToPastRunsActivity() {
        if (view.get() != null) {
            view.get()!!.launchPastRunsActivity()
        }
    }

    private suspend fun goalLevel() {
        repo.getTotalDistance().collect { d ->
            receivedTotalDistance(d ?: 0.0)
        }
    }

    private fun receivedTotalDistance(distance: Double) {
        if (view.get() != null) {
            when {
                distance < 100.0 -> { // Taragui
                    view.get()!!.setGoalTitle(R.string.taragui)
                    view.get()!!.setGoalSubtitle(R.string.taragui_subtitle)
                    view.get()!!.setGoalImage(R.drawable.taragui)
                }
                distance < 200.0 -> { // CBSé
                    view.get()!!.setGoalTitle(R.string.cbse)
                    view.get()!!.setGoalSubtitle(R.string.cbse_subtitle)
                    view.get()!!.setGoalImage(R.drawable.cbse)
                }
                distance < 300.0 -> { // Cruz de Malta
                    view.get()!!.setGoalTitle(R.string.cruz_de_malta)
                    view.get()!!.setGoalSubtitle(R.string.cruz_de_malta_subtitle)
                    view.get()!!.setGoalImage(R.drawable.cruzdemalta)
                }
                distance < 500.0 -> { // Playadito
                    view.get()!!.setGoalTitle(R.string.playadito)
                    view.get()!!.setGoalSubtitle(R.string.playadito_subtitle)
                    view.get()!!.setGoalImage(R.drawable.playadito)
                }
                distance < 750.0 -> { // Rosamonte
                    view.get()!!.setGoalTitle(R.string.rosamonte)
                    view.get()!!.setGoalSubtitle(R.string.rosamonte_subtitle)
                    view.get()!!.setGoalImage(R.drawable.rosamonte)
                }
                else -> { // La Merced
                    view.get()!!.setGoalTitle(R.string.merced)
                    view.get()!!.setGoalSubtitle(R.string.merced_subtitle)
                    view.get()!!.setGoalImage(R.drawable.lamerced)
                }
            }
        }
    }

    private fun onRunListErrorGoals(throwable: Throwable) {
        Timber.d("Failed to retrieve total distance from db")
        if (view.get() != null) {
            view.get()!!.setGoalTitle(R.string.taragui)
            view.get()!!.setGoalSubtitle(R.string.taragui_subtitle)
            view.get()!!.setGoalImage(R.drawable.taragui)
        }
    }

    fun goToAchievementsActivity() {
        if (view.get() != null) {
            view.get()!!.launchAchievementsActivity()
        }
    }

}