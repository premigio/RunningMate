package com.itba.runningMate.services.location

import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener
import com.itba.runningMate.services.location.listeners.OnTrackingMetricsUpdateListener
import com.itba.runningMate.services.location.listeners.OnTrackingUpdateListener

interface TrackingLocationUpdatesDispatcher {

    fun setOnTrackingUpdateListener(listener: OnTrackingUpdateListener)

    fun setOnTrackingLocationUpdateListener(listener: OnTrackingLocationUpdateListener)

    fun setOnTrackingMetricsUpdateListener(listener: OnTrackingMetricsUpdateListener)

    fun removeTrackingUpdateListener(listener: OnTrackingUpdateListener)

    fun removeTrackingLocationUpdateListener(listener: OnTrackingLocationUpdateListener)

    fun removeTrackingMetricsUpdateListener(listener: OnTrackingMetricsUpdateListener)

    fun callbackLocationUpdate(latitude: Double, longitude: Double)

    fun callbackDistanceUpdate(distance: Float)

    fun callbackPaceUpdate(pace: Long)

    fun callbackStopWatchUpdate(elapsedTime: Long)

    fun areMetricsUpdatesListener(): Boolean

    fun areLocationUpdatesListener(): Boolean

    fun areUpdatesListener(): Boolean

}