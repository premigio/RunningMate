package com.itba.runningMate.mainpage.fragments.running.ui;

import com.itba.runningMate.mainpage.fragments.running.model.Route;

public interface RunningView {

    void showRoute(Route route);

    void removeRoutes();

    void showLocation(double latitude, double longitude);

    void updateDistance(float elapsedDistance);

    void updateStopwatch(long elapsedTime);

    void updatePace(long pace);

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

    void showStopRunButton();

    void showStartRunButton();

    void showSaveRunError();

    void launchRunActivity(long runId);

    void showStopConfirm();
}
