package com.itba.runningMate.mainpage.fragments.running.ui;

import com.itba.runningMate.mainpage.fragments.running.model.Route;

public interface RunningView {

    void showRoute(Route route);

    void removeRoutes();

    void showLocation(double latitude, double longitude);

    void updateDistance(String elapsedDistance);

    void updateStopwatch(String elapsedTime);

    void updatePace(String pace);

    void showInitialMetrics();

    void showDefaultLocation();

    void mapEnableMyLocation();

    void mapDisableMyLocation();

    void launchAndAttachTrackingService();

    void detachTrackingService();

    void requestLocationPermission();

    boolean areLocationPermissionGranted();

    void showLocationPermissionNotGrantedError();

    void showLocationPermissionRationale();

    void showStopSprintButton();

    void showStartSprintButton();

    void showSaveSprintError();

    void launchSprintActivity(long sprintId);
}
