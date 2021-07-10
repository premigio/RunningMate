package com.itba.runningMate.achievements

import com.itba.runningMate.achievements.achievement.Achievements
import com.itba.runningMate.repository.achievements.AchievementsStorage
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.lang.ref.WeakReference

class AchievementsPresenter(
    private val repo: RunRepository,
    private val schedulerProvider: SchedulerProvider,
    private val storage: AchievementsStorage,
    view: AchievementsView
) {

    private val view: WeakReference<AchievementsView> = WeakReference(view)
    private val disposables: CompositeDisposable = CompositeDisposable()


    fun onViewAttached() {
        receivedTotalDistance(storage.getTotalDistance())
        achievements()
    }

    private fun achievements() {
        disposables.add(repo.getMaxSpeed()
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ speed: Double -> receivedMaxSpeed(speed) }) { throwable: Throwable ->
                onRunListErrorGoals()
            })
        disposables.add(repo.getMaxKcal()
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ kcal: Double -> receivedMaxKcal(kcal) }) { throwable: Throwable ->
                onRunListErrorGoals()
            })
        disposables.add(repo.getMaxTime()
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ time: Long -> receivedMaxTime(time) }) { throwable: Throwable ->
                onRunListErrorGoals()
            })
    }

    private fun receivedMaxSpeed(speed: Double) {
        view.get()?.setAchievement(Achievements.SPEED10, speed >= 10.0)
    }

    private fun receivedMaxKcal(kcal: Double) {
        view.get()?.setAchievement(Achievements.KCAL1000, kcal >= 1000.0)
    }

    private fun receivedMaxTime(time: Long) {
        view.get()?.setAchievement(Achievements.TIME1H, time >= 3600000)
    }

    fun onViewDetached() {
        disposables.dispose()
    }

    private fun receivedTotalDistance(distance: Double) {
        view.get()?.setAchievement(Achievements.DISTANCE2000, distance >= 2000.0)
    }

    private fun onRunListErrorGoals() {
        Timber.d("Failed to retrieve total distance from db")
    }

}
