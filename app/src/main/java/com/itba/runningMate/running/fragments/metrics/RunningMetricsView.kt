package com.itba.runningMate.running.fragments.metrics

interface RunningMetricsView {

    fun attachTrackingService()

    fun detachTrackingService()

    fun updateDistance(elapsedDistance: Float)

    fun updateCalories(calories: Int)

    fun updateStopwatch(elapsedTime: Long)

    fun updatePace(pace: Long)

    fun showInitialMetrics()

    fun showSaveRunError()

    fun launchRunActivity(runId: Long)

    fun finishActivity()

    fun showStopConfirm()

    fun showStopBtn()

    fun showPlayBtn()

    fun showPauseBtn()

    fun hideStopBtn()

    fun hidePlayBtn()

    fun hidePauseBtn()
}