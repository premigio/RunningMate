package com.itba.runningMate.levels

import com.itba.runningMate.domain.Level
import com.itba.runningMate.repository.aggregaterunmetrics.AggregateRunMetricsStorage
import io.reactivex.disposables.CompositeDisposable
import java.lang.ref.WeakReference

class LevelsPresenter(
    private val aggregateRunMetricsStorage: AggregateRunMetricsStorage,
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
        val distance = aggregateRunMetricsStorage.getTotalDistance()
        val level = Level.from(distance)
        view.get()?.showCurrentLevel(level, distance)
    }

}