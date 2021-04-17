package com.itba.runningMate.landing.repository;

public interface LandingStateStorage {

    String LANDING_STATE_PREFERENCES_FILE = "com.itba.runningMate.LANDING_STATE_PREFERENCES_FILE";

    boolean isCenterCamera();

    void setCenterCamera(final boolean centerCamera);

    double getLastKnownLatitude();

    double getLastKnownLongitude();

    boolean hasLastKnownLocation();

    void setLastKnownLocation(final double latitude, final double longitude);

    void persistState();

}
