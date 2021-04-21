package com.itba.runningMate.landing.ui;

import com.itba.runningMate.landing.model.Route;

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
