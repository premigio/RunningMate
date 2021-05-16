package com.itba.runningMate.running.fragments.metrics;

public interface RunningMetricsView {

    void attachTrackingService();

    void detachTrackingService();

    void updateDistance(float elapsedDistance);

    void updateStopwatch(long elapsedTime);

    void updatePace(long pace);

    void showInitialMetrics();

    void showSaveRunError();

    void launchRunActivity(long runId);

    void showStopConfirm();
}
