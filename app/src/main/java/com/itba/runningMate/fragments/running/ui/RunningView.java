package com.itba.runningMate.fragments.running.ui;

import com.itba.runningMate.fragments.running.model.Route;

public interface RunningView {

    void showRoute(Route route);

    void showLocation(double latitude, double longitude);

    void showDefaultLocation();

    void mapEnableMyLocation();

    void mapDisableMyLocation();

    void launchAndAttachTrackingService();

    void detachTrackingService();

    void requestLocationPermission();

    boolean areLocationPermissionGranted();

    void showLocationPermissionNotGrantedError();

    void showLocationPermissionRationale();
}
