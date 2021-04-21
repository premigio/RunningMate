package com.itba.runningMate.running.ui;

import com.itba.runningMate.running.model.Route;

public interface LandingView {

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
