package com.itba.runningMate.mainpage.fragments.running

import androidx.annotation.VisibleForTesting
import com.itba.runningMate.repository.runningstate.RunningStateStorage
import com.itba.runningMate.services.location.Tracker
import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener
import java.lang.ref.WeakReference

class RunningPresenter(
    private val stateStorage: RunningStateStorage,
    view: RunningView
) : OnTrackingLocationUpdateListener {

    private val view: WeakReference<RunningView> = WeakReference(view)

    @VisibleForTesting
    var tracker: Tracker? = null
        private set
    private var isTrackerAttached = false

    fun onViewAttached() {
        if (view.get() == null) {
            return
        }
        if (!view.get()!!.areLocationPermissionGranted()) {
            view.get()!!.requestLocationPermission()
        } else {
            view.get()!!.launchAndAttachTrackingService()
        }
    }

    fun onViewDetached() {
        stateStorage.persistState()
        if (isTrackerAttached && view.get() != null) {
            tracker!!.removeTrackingLocationUpdateListener(this)
            view.get()!!.detachTrackingService()
        }
    }

    fun onMapAttached() {
        if (view.get() == null) {
            return
        }
        if (view.get()!!.areLocationPermissionGranted()) {
            view.get()!!.mapEnableMyLocation()
            if (stateStorage.hasLastKnownLocation()) {
                view.get()!!
                    .showLocation(
                        stateStorage.getLastKnownLatitude(),
                        stateStorage.getLastKnownLongitude()
                    )
            }
        } else {
            view.get()!!.mapDisableMyLocation()
            view.get()!!.showDefaultLocation()
        }
    }

    fun onTrackingServiceAttached(tracker: Tracker) {
        this.tracker = tracker
        isTrackerAttached = true
        tracker.setOnTrackingLocationUpdateListener(this)
        if (tracker.isTracking()) {
            view.get()!!.launchRunningActivity()
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

    fun onRequestLocationPermissionResult(grantedPermission: Boolean) {
        if (view.get() == null) {
            return
        }
        if (grantedPermission) {
            view.get()!!.launchAndAttachTrackingService()
            onMapAttached()
        } else {
            view.get()!!.showLocationPermissionNotGrantedError()
        }
    }

    fun onStartButtonClick() {
        if (view.get() == null) {
            return
        }
        if (!view.get()!!.areLocationPermissionGranted()) {
            view.get()!!.requestLocationPermission()
        } else {
            if (isTrackerAttached && !tracker!!.isTracking()) {
                tracker!!.startTracking()
                view.get()!!.launchRunningActivity()
            }
        }
    }

    override fun onLocationUpdate(latitude: Double, longitude: Double) {
        if (view.get() == null) {
            return
        }
        if (stateStorage.isCenterCamera()) {
            view.get()!!.showLocation(latitude, longitude)
        }
        stateStorage.setLastKnownLocation(latitude, longitude)
    }

}