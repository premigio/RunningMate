package com.itba.runningMate.mainpage.fragments.running

interface RunningView {

    fun showLocation(latitude: Double, longitude: Double)

    fun showDefaultLocation()

    fun mapEnableMyLocation()

    fun mapDisableMyLocation()

    fun launchAndAttachTrackingService()

    fun detachTrackingService()

    fun requestLocationPermission()

    fun areLocationPermissionGranted(): Boolean

    fun showLocationPermissionNotGrantedError()

    fun showLocationPermissionRationale()

    fun launchRunningActivity()

}