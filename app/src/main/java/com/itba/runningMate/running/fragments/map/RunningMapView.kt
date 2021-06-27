package com.itba.runningMate.running.fragments.map;

import com.itba.runningMate.domain.Route;


public interface RunningMapView {

    void attachTrackingService();

    void detachTrackingService();

    void showRoute(Route route);

    void showDefaultLocation();

    void showLocation(double latitude, double longitude);

}
