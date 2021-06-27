package com.itba.runningMate.repository.runningstate;

public interface RunningStateStorage {

    String LANDING_STATE_PREFERENCES_FILE = "PREF_RUNNING_MATE_RUNNING_STATE";

    boolean isCenterCamera();

    void setCenterCamera(final boolean centerCamera);

    double getLastKnownLatitude();

    double getLastKnownLongitude();

    boolean hasLastKnownLocation();

    void setLastKnownLocation(final double latitude, final double longitude);

    void persistState();
}
