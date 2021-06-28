package com.itba.runningMate.achievements

import com.itba.runningMate.R
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
                onRunListErrorGoals(
                    throwable
                )
            })
        disposables.add(repo.getMaxKcal()
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ kcal: Double -> receivedMaxKcal(kcal) }) { throwable: Throwable ->
                onRunListErrorGoals(
                    throwable
                )
            })
        disposables.add(repo.getMaxTime()
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ time: Long -> receivedMaxTime(time) }) { throwable: Throwable ->
                onRunListErrorGoals(
                    throwable
                )
            })
    }

    private fun receivedMaxSpeed(speed: Double) {
        view.get()!!.setAchievement(Achievements.SPEED10, speed >= 10.0)
    }

    private fun receivedMaxKcal(kcal: Double) {
        view.get()!!.setAchievement(Achievements.KCAL1000, kcal >= 1000.0)
    }

    private fun receivedMaxTime(time: Long) {
        view.get()!!.setAchievement(Achievements.TIME1H, time >= 3600000)
    }

    fun onViewDetached() {
        disposables.dispose()
    }

    private fun receivedTotalDistance(distance: Double) {
        if (view.get() != null) {
            val progress = 0.0
            if (distance < 100.0) { // Taragui
                view.get()!!.setGoalTitle(R.string.taragui)
                view.get()!!.setGoalSubtitle(R.string.taragui_subtitle)
                view.get()!!.setGoalImage(R.drawable.taragui)
                view.get()!!.setProgressBar(distance, 100.0)
            } else if (distance < 200.0) { // CBSé
                view.get()!!.setGoalTitle(R.string.cbse)
                view.get()!!.setGoalSubtitle(R.string.cbse_subtitle)
                view.get()!!.setGoalImage(R.drawable.cbse)
                view.get()!!.setProgressBar(distance - 100.0, 100.0)
            } else if (distance < 300.0) { // Cruz de Malta
                view.get()!!.setGoalTitle(R.string.cruz_de_malta)
                view.get()!!.setGoalSubtitle(R.string.cruz_de_malta_subtitle)
                view.get()!!.setGoalImage(R.drawable.cruzdemalta)
                view.get()!!.setProgressBar(distance - 200.0, 100.0)
            } else if (distance < 500.0) { // Playadito
                view.get()!!.setGoalTitle(R.string.playadito)
                view.get()!!.setGoalSubtitle(R.string.playadito_subtitle)
                view.get()!!.setGoalImage(R.drawable.playadito)
                view.get()!!.setProgressBar(distance - 300.0, 200.0)
            } else if (distance < 750.0) { // Rosamonte
                view.get()!!.setGoalTitle(R.string.rosamonte)
                view.get()!!.setGoalSubtitle(R.string.rosamonte_subtitle)
                view.get()!!.setGoalImage(R.drawable.rosamonte)
                view.get()!!.setProgressBar(distance - 500.0, 250.0)
            } else { // La Merced
                view.get()!!.setGoalTitle(R.string.merced)
                view.get()!!.setGoalSubtitle(R.string.merced_subtitle)
                view.get()!!.setGoalImage(R.drawable.lamerced)
                view.get()!!.setProgressBar(100.0, 100.0)
            }
            //achievement 1 unlocked
            view.get()!!.setAchievement(Achievements.DISTANCE2000, distance >= 2000.0)
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

}