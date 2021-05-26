package com.itba.runningMate.running.fragments.metrics;

public interface RunningMetricsView {

    void attachTrackingService();

    void detachTrackingService();

    void updateDistance(float elapsedDistance);

    void updateCalories(int calories);

    void updateStopwatch(long elapsedTime);

    void updatePace(long pace);

    void showInitialMetrics();

    void showSaveRunError();

    void launchRunActivity(long runId);

    void showStopConfirm();

    void showStopBtn();

    void showPlayBtn();

    void showPauseBtn();

    void hideStopBtn();

    void hidePlayBtn();

    void hidePauseBtn();


}
