package com.itba.runningMate.repository.runningstate

interface RunningStateStorage {

    fun isCenterCamera(): Boolean

    fun setCenterCamera(centerCamera: Boolean)

    fun getLastKnownLatitude(): Double

    fun getLastKnownLongitude(): Double

    fun hasLastKnownLocation(): Boolean

    fun setLastKnownLocation(latitude: Double, longitude: Double)

    fun persistState()

    companion object {
        const val LANDING_STATE_PREFERENCES_FILE = "PREF_RUNNING_MATE_RUNNING_STATE"
    }
}