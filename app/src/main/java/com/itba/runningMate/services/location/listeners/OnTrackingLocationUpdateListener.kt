package com.itba.runningMate.services.location.listeners

interface OnTrackingLocationUpdateListener {

    fun onLocationUpdate(latitude: Double, longitude: Double)

}