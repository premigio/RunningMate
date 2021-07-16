package com.itba.runningMate.achievements

import com.itba.runningMate.achievements.model.AggregateRunMetricsDetail
import com.itba.runningMate.domain.Achievements
import com.itba.runningMate.repository.achievements.AchievementsStorage
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.Single
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
        achievements()
    }

    private fun achievements() {
        disposables.add(Single.zip(repo.getMaxSpeed(), repo.getMaxKcal(), repo.getMaxTime(),
            { maxSpeed, maxKcal, maxTime ->
                AggregateRunMetricsDetail.Builder()
                    .speed(maxSpeed.toFloat())
                    .calories(maxKcal.toInt())
                    .runningTime(maxTime)
                    .build()
            })
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ aggregate: AggregateRunMetricsDetail -> receivedAggregate(aggregate) }) { onRunListErrorGoals() }
        )
    }

    private fun receivedAggregate(aggregate: AggregateRunMetricsDetail) {
        aggregate.distance = storage.getTotalDistance().toFloat()
        val completedAchievements: MutableList<Achievements> = mutableListOf()
        for (a in Achievements.values()) {
            if (a.completed(aggregate)) {
                completedAchievements.add(a)
            }
        }
        view.get()?.showAchievements(completedAchievements.toTypedArray())
    }

    fun onViewDetached() {
        disposables.dispose()
    }

    private fun onRunListErrorGoals() {
        Timber.d("Failed to retrieve total distance from db")
    }

}
