package com.itba.runningMate.running.fragments.map

import com.itba.runningMate.domain.Route

interface RunningMapView {

    fun attachTrackingService()

    fun detachTrackingService()

    fun showRoute(route: Route)

    fun showDefaultLocation()

    fun showLocation(latitude: Double, longitude: Double)

}