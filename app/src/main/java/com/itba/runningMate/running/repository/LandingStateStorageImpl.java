package com.itba.runningMate.running.repository;

import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

public class LandingStateStorageImpl implements LandingStateStorage {

    public static final double DEFAULT_LATITUDE = -34.5997;
    public static final double DEFAULT_LONGITUDE = -58.3819;
    public static final String KEY_CENTER_CAMERA = "is_camera_centered";
    public static final String KEY_LOCATION_LATITUDE = "location_lat";
    public static final String KEY_LOCATION_LONGITUDE = "location_long";

    private SharedPreferences preferences;

    private Boolean centerCamera;
    private Double lastKnownLatitude;
    private Double lastKnownLongitude;

    public LandingStateStorageImpl(final SharedPreferences preferences) {
        this.preferences = preferences;
        centerCamera = preferences.getBoolean(KEY_CENTER_CAMERA, true);
        if (preferences.contains(KEY_LOCATION_LATITUDE)) {
            lastKnownLongitude = (double) preferences.getFloat(KEY_LOCATION_LATITUDE, (float) DEFAULT_LATITUDE);
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
            editor.putFloat(KEY_LOCATION_LATITUDE, lastKnownLongitude.floatValue());
        }
        if (centerCamera != null) {
            editor.putBoolean(KEY_CENTER_CAMERA, centerCamera);
        }
        editor.apply();
    }
}
