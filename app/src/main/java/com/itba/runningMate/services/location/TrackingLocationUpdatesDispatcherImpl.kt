package com.itba.runningMate.services.location

import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener
import com.itba.runningMate.services.location.listeners.OnTrackingMetricsUpdateListener
import com.itba.runningMate.services.location.listeners.OnTrackingUpdateListener
import com.itba.runningMate.utils.functional.Function
import java.lang.ref.WeakReference

class TrackingLocationUpdatesDispatcherImpl : TrackingLocationUpdatesDispatcher {

    private var onTrackingMetricsUpdateListeners: MutableList<WeakReference<OnTrackingMetricsUpdateListener>> =
        mutableListOf()
    private var onTrackingLocationsUpdateListeners: MutableList<WeakReference<OnTrackingLocationUpdateListener>> =
        mutableListOf()

    override fun setOnTrackingUpdateListener(listener: OnTrackingUpdateListener) {
        setOnTrackingLocationUpdateListener(listener)
        setOnTrackingMetricsUpdateListener(listener)
    }

    override fun setOnTrackingLocationUpdateListener(listener: OnTrackingLocationUpdateListener) {
        onTrackingLocationsUpdateListeners.add(WeakReference(listener))
    }

    override fun setOnTrackingMetricsUpdateListener(listener: OnTrackingMetricsUpdateListener) {
        onTrackingMetricsUpdateListeners.add(WeakReference(listener))
    }

    override fun removeTrackingUpdateListener(listener: OnTrackingUpdateListener) {
        removeTrackingLocationUpdateListener(listener)
        removeTrackingMetricsUpdateListener(listener)
    }

    override fun removeTrackingLocationUpdateListener(listener: OnTrackingLocationUpdateListener) {
        removeListeners(onTrackingLocationsUpdateListeners, listener)
    }

    override fun removeTrackingMetricsUpdateListener(listener: OnTrackingMetricsUpdateListener) {
        removeListeners(onTrackingMetricsUpdateListeners, listener)
    }

    private fun <T> removeListeners(listeners: MutableList<WeakReference<T>>, listener: T) {
        val iterator = listeners.iterator()
        while (iterator.hasNext()) {
            val wr = iterator.next()
            if (wr.get() === listener) {
                iterator.remove()
            }
        }
    }

    override fun areMetricsUpdatesListener(): Boolean {
        return areListeners(onTrackingMetricsUpdateListeners)
    }

    override fun areLocationUpdatesListener(): Boolean {
        return areListeners(onTrackingLocationsUpdateListeners)
    }

    override fun areUpdatesListener(): Boolean {
        return areLocationUpdatesListener() || areMetricsUpdatesListener()
    }

    private fun <T> areListeners(listeners: MutableList<WeakReference<T>>): Boolean {
        val iterator = listeners.iterator()
        while (iterator.hasNext()) {
            val wr = iterator.next()
            if (wr.get() == null) {
                iterator.remove()
            } else {
                return true
            }
        }
        return false
    }

    private fun <T> callListeners(listeners: MutableList<WeakReference<T>>, function: Function<T>) {
        val iterator = listeners.iterator()
        while (iterator.hasNext()) {
            val wr = iterator.next()
            if (wr.get() == null) {
                iterator.remove()
            } else {
                function.apply(wr.get()!!)
            }
        }
    }

    override fun callbackLocationUpdate(latitude: Double, longitude: Double) {
        callListeners(
            onTrackingLocationsUpdateListeners
        ) { l: OnTrackingLocationUpdateListener ->
            l.onLocationUpdate(
                latitude,
                longitude
            )
        }
    }

    override fun callbackDistanceUpdate(distance: Float) {
        callListeners(
            onTrackingMetricsUpdateListeners
        ) { l: OnTrackingMetricsUpdateListener -> l.onDistanceUpdate(distance) }
    }

    override fun callbackPaceUpdate(pace: Long) {
        callListeners(
            onTrackingMetricsUpdateListeners
        ) { l: OnTrackingMetricsUpdateListener -> l.onPaceUpdate(pace) }
    }

    override fun callbackStopWatchUpdate(elapsedTime: Long) {
        callListeners(
            onTrackingMetricsUpdateListeners
        ) { l: OnTrackingMetricsUpdateListener -> l.onStopWatchUpdate(elapsedTime) }
    }

}