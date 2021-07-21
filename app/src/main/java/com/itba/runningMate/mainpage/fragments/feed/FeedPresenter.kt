package com.itba.runningMate.mainpage.fragments.feed

import com.itba.runningMate.domain.Achievements
import com.itba.runningMate.domain.Level
import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.achievements.AchievementsRepository
import com.itba.runningMate.repository.aggregaterunmetrics.AggregateRunMetricsStorage
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.lang.ref.WeakReference

class FeedPresenter(
    private val repo: RunRepository,
    private val achievementsRepository: AchievementsRepository,
    private val schedulerProvider: SchedulerProvider,
    private val aggregateRunMetricsStorage: AggregateRunMetricsStorage,
    view: FeedView
) {

    private val view: WeakReference<FeedView> = WeakReference(view)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun onViewAttached() {
        view.get()?.startRecentActivityShimmerAnimation()
        view.get()?.startLevelShimmerAnimation()
        view.get()?.startAchievementsShimmerAnimation()

        recentActivity()
        level()
        achievements()
    }

    fun onViewDetached() {
        disposables.clear()
    }

    private fun onRunListError() {
        Timber.d("Failed to retrieve runs from db")
        view.get()?.showRecentActivity(listOf())
    }

    private fun receivedRunList(runs: List<Run>) {
        view.get()?.stopRecentActivityShimmerAnimation()
        view.get()?.showRecentActivity(runs)
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
            .subscribe({ runs: List<Run> -> receivedRunList(runs) }) { onRunListError() })
    }

    private fun level() {
        if (view.get() != null) {
            view.get()?.stopLevelShimmerAnimation()
            val distance = aggregateRunMetricsStorage.getTotalDistance()
            val level = Level.from(distance)
            view.get()!!.showCurrentLevel(level, distance)
        }
    }

    private fun achievements() {
        disposables.add(
            achievementsRepository.getAchievements(3)
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe({ achievements: List<Achievements> -> receivedAchievements(achievements) }) { onReceivedAchievementsError() }
        )
    }

    private fun receivedAchievements(latestCompletedAchievements: List<Achievements>) {
        view.get()?.stopAchievementsShimmerAnimation()
        view.get()?.showAchievements(latestCompletedAchievements)
    }

    private fun onReceivedAchievementsError() {
        Timber.d("Failed to retrieve completed achievements from db")
        view.get()?.showAchievements(listOf())
    }

    fun goToAchievementsActivity() {
        view.get()?.launchAchievementsActivity()
    }

    fun goToLevelsActivity() {
        view.get()?.launchLevelsActivity()
    }

}
