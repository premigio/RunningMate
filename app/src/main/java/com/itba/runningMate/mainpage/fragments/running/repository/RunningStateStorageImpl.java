package com.itba.runningMate.mainpage.fragments.running.repository;

import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import static com.itba.runningMate.Constants.DEFAULT_LATITUDE;
import static com.itba.runningMate.Constants.DEFAULT_LONGITUDE;

public class RunningStateStorageImpl implements RunningStateStorage {

    public static final String KEY_CENTER_CAMERA = "is_camera_centered";
    public static final String KEY_LOCATION_LATITUDE = "location_lat";
    public static final String KEY_LOCATION_LONGITUDE = "location_long";

    private SharedPreferences preferences;

    private Boolean centerCamera;
    private Double lastKnownLatitude;
    private Double lastKnownLongitude;

    public RunningStateStorageImpl(final SharedPreferences preferences) {
        this.preferences = preferences;
        centerCamera = preferences.getBoolean(KEY_CENTER_CAMERA, true);
        if (preferences.contains(KEY_LOCATION_LATITUDE)) {
            lastKnownLatitude = (double) preferences.getFloat(KEY_LOCATION_LATITUDE, (float) DEFAULT_LATITUDE);
        }
        if (preferences.contains(KEY_LOCATION_LONGITUDE)) {
            lastKnownLongitude = (double) preferences.getFloat(KEY_LOCATION_LONGITUDE, (float) DEFAULT_LONGITUDE);
        }
    }

    public boolean isCenterCamera() {
        return centerCamera;
    }

    public void setCenterCamera(final boolean centerCamera) {
        this.centerCamera = centerCamera;
    }

    public LatLng getLastKnownLocation() {
        if (hasLastKnownLocation()) {
            return new LatLng(lastKnownLatitude, lastKnownLongitude);
        } else {
            return new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        }
    }

    public void setLastKnownLocation(final double latitude, final double longitude) {
        this.lastKnownLatitude = latitude;
        this.lastKnownLongitude = longitude;
    }

    @Override
    public double getLastKnownLatitude() {
        return this.lastKnownLatitude;
    }

    @Override
    public double getLastKnownLongitude() {
        return this.lastKnownLongitude;
    }

    @Override
    public boolean hasLastKnownLocation() {
        return lastKnownLongitude != null && lastKnownLatitude != null;
    }

    @Override
    public void persistState() {
        SharedPreferences.Editor editor = preferences.edit();
        if (lastKnownLatitude != null) {
            editor.putFloat(KEY_LOCATION_LATITUDE, lastKnownLatitude.floatValue());
        }
        if (lastKnownLongitude != null) {
            editor.putFloat(KEY_LOCATION_LONGITUDE, lastKnownLongitude.floatValue());
        }
        if (centerCamera != null) {
            editor.putBoolean(KEY_CENTER_CAMERA, centerCamera);
        }
        editor.apply();
    }
}
