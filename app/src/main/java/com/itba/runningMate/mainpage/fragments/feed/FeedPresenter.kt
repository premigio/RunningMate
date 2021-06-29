package com.itba.runningMate.mainpage.fragments.feed

import com.itba.runningMate.R
import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.lang.ref.WeakReference

class FeedPresenter(
    private val repo: RunRepository,
    private val schedulerProvider: SchedulerProvider,
    view: FeedView
) {

    private val view: WeakReference<FeedView> = WeakReference(view)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun onViewAttached() {
        disposables.add(repo.getRunLazy()
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ runs: List<Run> -> receivedRunList(runs) }) { throwable: Throwable ->
                onRunListError(
                    throwable
                )
            })
        goalLevel()
    }

    fun onViewDetached() {
        disposables.clear()
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
            val maxVal = Math.min(runs.size, 3)
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

    private fun goalLevel() {
        disposables.add(repo.getTotalDistance()
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ distance: Double -> receivedTotalDistance(distance) }) { throwable: Throwable ->
                onRunListErrorGoals(
                    throwable
                )
            })
    }

    private fun receivedTotalDistance(distance: Double) {
        if (view.get() != null) {
            if (distance < 100.0) { // Taragui
                view.get()!!.setGoalTitle(R.string.taragui)
                view.get()!!.setGoalSubtitle(R.string.taragui_subtitle)
                view.get()!!.setGoalImage(R.drawable.taragui)
            } else if (distance < 200.0) { // CBSé
                view.get()!!.setGoalTitle(R.string.cbse)
                view.get()!!.setGoalSubtitle(R.string.cbse_subtitle)
                view.get()!!.setGoalImage(R.drawable.cbse)
            } else if (distance < 300.0) { // Cruz de Malta
                view.get()!!.setGoalTitle(R.string.cruz_de_malta)
                view.get()!!.setGoalSubtitle(R.string.cruz_de_malta_subtitle)
                view.get()!!.setGoalImage(R.drawable.cruzdemalta)
            } else if (distance < 500.0) { // Playadito
                view.get()!!.setGoalTitle(R.string.playadito)
                view.get()!!.setGoalSubtitle(R.string.playadito_subtitle)
                view.get()!!.setGoalImage(R.drawable.playadito)
            } else if (distance < 750.0) { // Rosamonte
                view.get()!!.setGoalTitle(R.string.rosamonte)
                view.get()!!.setGoalSubtitle(R.string.rosamonte_subtitle)
                view.get()!!.setGoalImage(R.drawable.rosamonte)
            } else { // La Merced
                view.get()!!.setGoalTitle(R.string.merced)
                view.get()!!.setGoalSubtitle(R.string.merced_subtitle)
                view.get()!!.setGoalImage(R.drawable.lamerced)
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