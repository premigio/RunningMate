package com.itba.runningMate.achievements

import com.itba.runningMate.domain.Achievements
import com.itba.runningMate.repository.achievements.AchievementsRepository
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.lang.ref.WeakReference

class AchievementsPresenter(
    private val schedulerProvider: SchedulerProvider,
    private val achievementsRepository: AchievementsRepository,
    view: AchievementsView
) {

    private val view: WeakReference<AchievementsView> = WeakReference(view)
    private val disposables: CompositeDisposable = CompositeDisposable()


    fun onViewAttached() {
        achievements()
    }

    private fun receivedAchievements(latestCompletedAchievements: List<Achievements>) {
        view.get()?.showAchievements(latestCompletedAchievements)
    }

    private fun onReceivedAchievementsError() {
        Timber.d("Failed to retrieve completed achievements from db")
    }

    private fun achievements() {
        disposables.add(
            achievementsRepository.getAchievements()
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe({ achievements: List<Achievements> -> receivedAchievements(achievements) }) { onReceivedAchievementsError() }
        )
    }

    fun onViewDetached() {
        disposables.dispose()
    }

}
