package com.itba.runningMate.mainpage.fragments.running;

public interface RunningView {

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

    void launchRunningActivity();

}
