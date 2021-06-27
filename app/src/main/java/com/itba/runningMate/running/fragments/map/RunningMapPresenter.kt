package com.itba.runningMate.running.fragments.map

import androidx.annotation.VisibleForTesting
import com.itba.runningMate.domain.Route
import com.itba.runningMate.repository.runningstate.RunningStateStorage
import com.itba.runningMate.services.location.Tracker
import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener
import java.lang.ref.WeakReference

class RunningMapPresenter(private val stateStorage: RunningStateStorage, view: RunningMapView?) :
    OnTrackingLocationUpdateListener {

    private val view: WeakReference<RunningMapView> = WeakReference(view)

    @get:VisibleForTesting
    var tracker: Tracker? = null
        private set

    @get:VisibleForTesting
    var isTrackerAttached = false
        private set

    fun onViewAttached() {
        if (view.get() == null) {
            return
        }
        view.get()!!.attachTrackingService()
    }

    fun onViewDetached() {
        stateStorage.persistState()
        if (isTrackerAttached) {
            tracker!!.removeTrackingLocationUpdateListener(this)
        }
        if (view.get() != null) {
            view.get()!!.detachTrackingService()
        }
    }

    fun onMapAttached() {
        if (view.get() == null) {
            return
        }
        if (stateStorage.hasLastKnownLocation()) {
            view.get()!!
                .showLocation(
                    stateStorage.getLastKnownLatitude(),
                    stateStorage.getLastKnownLongitude()
                )
        } else {
            view.get()!!.showDefaultLocation()
        }
    }

    fun onTrackingServiceAttached(tracker: Tracker) {
        this.tracker = tracker
        isTrackerAttached = true
        tracker.setOnTrackingLocationUpdateListener(this)
        if (tracker.isTracking() && view.get() != null) {
            // recuperamos la ruta y actualizamos LastKnownLocation
            val route = tracker.queryRoute()
            if (!route.isEmpty) {
                stateStorage.setLastKnownLocation(route.lastLatitude, route.lastLongitude)
                view.get()!!.showRoute(route)
            }
        }
    }

    fun onTrackingServiceDetached() {
        tracker = null
        isTrackerAttached = false
    }

    fun centerCamera() {
        stateStorage.setCenterCamera(true)
        if (stateStorage.hasLastKnownLocation()) {
            view.get()!!
                .showLocation(
                    stateStorage.getLastKnownLatitude(),
                    stateStorage.getLastKnownLongitude()
                )
        }
    }

    fun freeCamera() {
        stateStorage.setCenterCamera(false)
    }

    override fun onLocationUpdate(latitude: Double, longitude: Double) {
        if (isTrackerAttached && tracker!!.isTracking() && view.get() != null) {
            if (stateStorage.hasLastKnownLocation()) {
                val route = Route()
                    .addToRoute(
                        stateStorage.getLastKnownLatitude(),
                        stateStorage.getLastKnownLongitude()
                    )
                    .addToRoute(latitude, longitude)
                view.get()!!.showRoute(route)
            }
        }
        if (stateStorage.isCenterCamera() && view.get() != null) {
            view.get()!!.showLocation(latitude, longitude)
        }
        stateStorage.setLastKnownLocation(latitude, longitude)
    }

}