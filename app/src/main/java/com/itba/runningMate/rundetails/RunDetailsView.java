package com.itba.runningMate.rundetails;

import android.graphics.Bitmap;
import android.net.Uri;

import com.itba.runningMate.domain.Route;

public interface RunDetailsView {

    void showRoute(Route route);

    void showSpeed(String speed);

    void showPace(String pace);

    void showDistance(String distance);

    void showTitle(String title);

    void showRunTimeInterval(String startTime);

    void showElapsedTime(String elapsedTime);

    void showRunningTime(String runningTime);

    void endActivity();

    void shareImageIntent(Uri uri);

    Bitmap getMetricsImage();

    void showShareRunError();

    void showUpdateTitleError();

    void showDeleteError();

}
