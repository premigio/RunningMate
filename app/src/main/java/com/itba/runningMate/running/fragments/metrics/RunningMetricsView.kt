package com.itba.runningMate.running.fragments.metrics

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions

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
    fun getGoogleFitPermissions(account: GoogleSignInAccount, fitApiClient: FitnessOptions)
    fun isUserLoggedFit(account: GoogleSignInAccount, fitApiClient: FitnessOptions): Boolean
    fun getFitAccount(fitApiClient: FitnessOptions): GoogleSignInAccount
    fun buildFitness(): FitnessOptions
    fun getStepCount(startTime: Long, endTime: Long)
}