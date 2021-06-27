package com.itba.runningMate.repository.runningstate

import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng
import com.itba.runningMate.utils.Constants.DEFAULT_LATITUDE
import com.itba.runningMate.utils.Constants.DEFAULT_LONGITUDE

class RunningStateStorageImpl(private val preferences: SharedPreferences) : RunningStateStorage {

    private var centerCamera: Boolean
    private var lastKnownLatitude: Double? = null
    private var lastKnownLongitude: Double? = null

    override fun isCenterCamera(): Boolean {
        return centerCamera
    }

    override fun setCenterCamera(centerCamera: Boolean) {
        this.centerCamera = centerCamera
    }

    val lastKnownLocation: LatLng
        get() = if (hasLastKnownLocation()) {
            LatLng(lastKnownLatitude!!, lastKnownLongitude!!)
        } else {
            LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
        }

    override fun setLastKnownLocation(latitude: Double, longitude: Double) {
        lastKnownLatitude = latitude
        lastKnownLongitude = longitude
    }

    override fun getLastKnownLatitude(): Double {
        return lastKnownLatitude!!
    }

    override fun getLastKnownLongitude(): Double {
        return lastKnownLongitude!!
    }

    override fun hasLastKnownLocation(): Boolean {
        return lastKnownLongitude != null && lastKnownLatitude != null
    }

    override fun persistState() {
        val editor = preferences.edit()
        if (lastKnownLatitude != null) {
            editor.putFloat(KEY_LOCATION_LATITUDE, lastKnownLatitude!!.toFloat())
        }
        if (lastKnownLongitude != null) {
            editor.putFloat(KEY_LOCATION_LONGITUDE, lastKnownLongitude!!.toFloat())
        }
        if (centerCamera != null) {
            editor.putBoolean(KEY_CENTER_CAMERA, centerCamera)
        }
        editor.apply()
    }

    companion object {
        const val KEY_CENTER_CAMERA = "is_camera_centered"
        const val KEY_LOCATION_LATITUDE = "location_lat"
        const val KEY_LOCATION_LONGITUDE = "location_long"
    }

    init {
        centerCamera = preferences.getBoolean(KEY_CENTER_CAMERA, true)
        if (preferences.contains(KEY_LOCATION_LATITUDE)) {
            lastKnownLatitude = preferences.getFloat(
                KEY_LOCATION_LATITUDE,
                DEFAULT_LATITUDE.toFloat()
            ).toDouble()
        }
        if (preferences.contains(KEY_LOCATION_LONGITUDE)) {
            lastKnownLongitude = preferences.getFloat(
                KEY_LOCATION_LONGITUDE,
                DEFAULT_LONGITUDE.toFloat()
            ).toDouble()
        }
    }
}