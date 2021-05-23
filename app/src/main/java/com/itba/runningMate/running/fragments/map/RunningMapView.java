package com.itba.runningMate.running.fragments.map;

import com.itba.runningMate.mainpage.fragments.running.model.Route;


public interface RunningMapView {

    void attachTrackingService();

    void detachTrackingService();

    void showRoute(Route route);

    void showDefaultLocation();

    void showLocation(double latitude, double longitude);

}
