package com.itba.runningMate.fragments.running.ui;

import com.itba.runningMate.fragments.running.model.Route;

public interface RunningView {

    void showRoute(Route route);

    void removeRoutes();

    void showLocation(double latitude, double longitude);

    void updateDistanceTextView(String elapsedDistance);

    void updateStopwatchTextView(String elapsedTime);

    void updatePaceTextView(String pace);

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
