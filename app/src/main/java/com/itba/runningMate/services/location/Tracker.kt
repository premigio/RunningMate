package com.itba.runningMate.services.location

import com.itba.runningMate.domain.Route
import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener
import com.itba.runningMate.services.location.listeners.OnTrackingMetricsUpdateListener
import com.itba.runningMate.services.location.listeners.OnTrackingUpdateListener

interface Tracker {

    fun startTracking()

    fun stopTracking()

    fun newLap()

    fun resumeTracking()

    fun queryRoute(): Route

    fun queryStartTime(): Long

    fun queryEndTime(): Long

    fun queryDistance(): Float

    fun queryElapsedTime(): Long

    fun queryPace(): Long

    fun queryVelocity(): Float

    val isTracking: Boolean

    fun setOnTrackingUpdateListener(listener: OnTrackingUpdateListener)

    fun setOnTrackingLocationUpdateListener(listener: OnTrackingLocationUpdateListener)

    fun setTrackingMetricsUpdateListener(listener: OnTrackingMetricsUpdateListener)

    fun removeTrackingUpdateListener(listener: OnTrackingUpdateListener)

    fun removeTrackingLocationUpdateListener(listener: OnTrackingLocationUpdateListener)

    fun removeTrackingMetricsUpdateListener(listener: OnTrackingMetricsUpdateListener)

}