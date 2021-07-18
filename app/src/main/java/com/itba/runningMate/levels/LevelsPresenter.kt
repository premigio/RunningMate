package com.itba.runningMate.levels

import com.itba.runningMate.domain.Level
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.lang.ref.WeakReference

class LevelsPresenter(
    private val repo: RunRepository,
    private val schedulerProvider: SchedulerProvider,
    view: LevelsView
) {

    private val view: WeakReference<LevelsView> = WeakReference(view)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun onViewAttached() {
        level()
    }

    fun onViewDetached() {
        disposables.clear()
    }

    private fun level() {
        disposables.add(repo.getTotalDistance()
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ distance: Double -> receivedTotalDistance(distance) }) { onReceivedTotalDistanceError() })
    }

    private fun receivedTotalDistance(distance: Double) {
        val level = Level.from(distance)
        view.get()?.showCurrentLevel(level, distance)
    }

    private fun onReceivedTotalDistanceError() {
        Timber.d("Failed to retrieve total distance from db")
    }
}