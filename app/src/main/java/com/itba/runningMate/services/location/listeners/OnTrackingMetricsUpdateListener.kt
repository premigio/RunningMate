package com.itba.runningMate.services.location.listeners

interface OnTrackingMetricsUpdateListener {

    fun onStopWatchUpdate(elapsedTime: Long)

    fun onDistanceUpdate(elapsedDistance: Float)

    fun onPaceUpdate(pace: Long)

}