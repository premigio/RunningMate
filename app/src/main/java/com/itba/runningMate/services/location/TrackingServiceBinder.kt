package com.itba.runningMate.services.location

import android.os.Binder
import com.itba.runningMate.domain.Route
import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener
import com.itba.runningMate.services.location.listeners.OnTrackingMetricsUpdateListener
import com.itba.runningMate.services.location.listeners.OnTrackingUpdateListener
import com.itba.runningMate.utils.run.RunMetrics

class TrackingServiceBinder(private val trackingService: TrackingService) : Binder(), Tracker {

    override fun queryRoute(): Route {
        return Route().addLatLngsToRoute(trackingService.getTrackedLocations())
    }

    override fun isTracking(): Boolean {
        return trackingService.isTracking
    }

    override fun startTracking() {
        trackingService.startTracking()
    }

    override fun stopTracking() {
        trackingService.stopTracking()
    }

    override fun newLap() {
        trackingService.newLap()
    }

    override fun resumeTracking() {
        trackingService.resumeTracking()
    }

    override fun setOnTrackingUpdateListener(listener: OnTrackingUpdateListener) {
        trackingService.setOnTrackingUpdateListener(listener)
    }

    override fun setOnTrackingLocationUpdateListener(listener: OnTrackingLocationUpdateListener) {
        trackingService.setOnTrackingLocationUpdateListener(listener)
    }

    override fun setTrackingMetricsUpdateListener(listener: OnTrackingMetricsUpdateListener) {
        trackingService.setOnTrackingMetricsUpdateListener(listener)
    }

    override fun removeTrackingUpdateListener(listener: OnTrackingUpdateListener) {
        trackingService.removeTrackingUpdateListener(listener)
    }

    override fun removeTrackingLocationUpdateListener(listener: OnTrackingLocationUpdateListener) {
        trackingService.removeTrackingLocationUpdateListener(listener)
    }

    override fun removeTrackingMetricsUpdateListener(listener: OnTrackingMetricsUpdateListener) {
        trackingService.removeTrackingMetricsUpdateListener(listener)
    }

    override fun queryStartTime(): Long {
        return trackingService.startTimeMillis
    }

    override fun queryEndTime(): Long {
        return trackingService.endTimeMillis
    }

    override fun queryDistance(): Float {
        return trackingService.elapsedDistance
    }

    override fun queryElapsedTime(): Long {
        return trackingService.runningMillis
    }

    override fun queryPace(): Long {
        return trackingService.pace
    }

    override fun queryVelocity(): Float {
        return RunMetrics.calculateVelocity(
            trackingService.elapsedDistance,
            trackingService.runningMillis
        )
    }
}