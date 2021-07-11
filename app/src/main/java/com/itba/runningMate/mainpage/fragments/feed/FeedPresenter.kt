package com.itba.runningMate.mainpage.fragments.feed

import com.itba.runningMate.domain.Level
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
        view.get()?.startLevelShimmerAnimation()
        view.get()?.startRecentActivityShimmerAnimation()
        recentActivity()
        level()
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
            view.get()?.stopRecentActivityShimmerAnimation()
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

    private fun recentActivity() {
        /*
            ver -->
            https://medium.com/default-to-open/smooth-loading-animations-in-android-11dcae4ecfd0
        */
        disposables.add(repo.getRunLazy()
//            Para cancherear un rato
//            .debounce(2, TimeUnit.SECONDS)
            .limit(3)
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ runs: List<Run> -> receivedRunList(runs) }) { throwable: Throwable ->
                onRunListError(
                    throwable
                )
            })
    }

    private fun level() {
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
            view.get()?.stopLevelShimmerAnimation()
            val level = Level.from(distance)
            view.get()!!.setGoalTitle(level.title)
            view.get()!!.setGoalSubtitle(level.subTitle)
            view.get()!!.setGoalImage(level.image)
        }
    }

    private fun onRunListErrorGoals(throwable: Throwable) {
        Timber.d("Failed to retrieve total distance from db")
    }

    fun goToAchievementsActivity() {
        if (view.get() != null) {
            view.get()!!.launchAchievementsActivity()
        }
    }

    fun goToLevelsActivity() {
        view.get()?.launchLevelsActivity()
    }

}