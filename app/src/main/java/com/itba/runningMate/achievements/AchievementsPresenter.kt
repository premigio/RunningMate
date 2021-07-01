package com.itba.runningMate.achievements

import com.itba.runningMate.R
import com.itba.runningMate.achievements.achievement.Achievements
import com.itba.runningMate.repository.achievements.AchievementsStorage
import com.itba.runningMate.repository.run.RunRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class AchievementsPresenter(
        private val repo: RunRepository,
        private val storage: AchievementsStorage,
        private val scope: CoroutineScope,
        view: AchievementsView
) {

    private val view: WeakReference<AchievementsView> = WeakReference(view)
    private lateinit var mainJob: Job

    fun onViewAttached() {
        receivedTotalDistance(storage.getTotalDistance())
        achievements()
    }

    private fun achievements() {
        mainJob = scope.launch {
            launch {
                val speed = repo.getMaxSpeed() ?: 0.0
                receivedMaxSpeed(speed)
            }
            launch {
                val time = repo.getMaxTime() ?: 0L
                receivedMaxTime(time)
            }
            launch {
                val kcal = repo.getMaxKcal() ?: 0.0
                receivedMaxKcal(kcal)
            }
        }
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
        mainJob.cancel()
    }

    private fun receivedTotalDistance(distance: Double) {
        if (view.get() != null) {
            when {
                distance < 100.0 -> { // Taragui
                    view.get()!!.setGoalTitle(R.string.taragui)
                    view.get()!!.setGoalSubtitle(R.string.taragui_subtitle)
                    view.get()!!.setGoalImage(R.drawable.taragui)
                    view.get()!!.setProgressBar(distance, 100.0)
                }
                distance < 200.0 -> { // CBSé
                    view.get()!!.setGoalTitle(R.string.cbse)
                    view.get()!!.setGoalSubtitle(R.string.cbse_subtitle)
                    view.get()!!.setGoalImage(R.drawable.cbse)
                    view.get()!!.setProgressBar(distance - 100.0, 100.0)
                }
                distance < 300.0 -> { // Cruz de Malta
                    view.get()!!.setGoalTitle(R.string.cruz_de_malta)
                    view.get()!!.setGoalSubtitle(R.string.cruz_de_malta_subtitle)
                    view.get()!!.setGoalImage(R.drawable.cruzdemalta)
                    view.get()!!.setProgressBar(distance - 200.0, 100.0)
                }
                distance < 500.0 -> { // Playadito
                    view.get()!!.setGoalTitle(R.string.playadito)
                    view.get()!!.setGoalSubtitle(R.string.playadito_subtitle)
                    view.get()!!.setGoalImage(R.drawable.playadito)
                    view.get()!!.setProgressBar(distance - 300.0, 200.0)
                }
                distance < 750.0 -> { // Rosamonte
                    view.get()!!.setGoalTitle(R.string.rosamonte)
                    view.get()!!.setGoalSubtitle(R.string.rosamonte_subtitle)
                    view.get()!!.setGoalImage(R.drawable.rosamonte)
                    view.get()!!.setProgressBar(distance - 500.0, 250.0)
                }
                else -> { // La Merced
                    view.get()!!.setGoalTitle(R.string.merced)
                    view.get()!!.setGoalSubtitle(R.string.merced_subtitle)
                    view.get()!!.setGoalImage(R.drawable.lamerced)
                    view.get()!!.setProgressBar(100.0, 100.0)
                }
            }
            //achievement 1 unlocked
            view.get()!!.setAchievement(Achievements.DISTANCE2000, distance >= 2000.0)
        }
    }

}