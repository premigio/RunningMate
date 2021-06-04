package com.itba.runningMate.rundetails;

import android.graphics.Bitmap;
import android.net.Uri;

import com.itba.runningMate.domain.Route;

public interface RunDetailsView {

    void showRoute(Route route);

    void showSpeed(String speed);

    void showPace(String pace);

    void showDistance(String distance);

    void showStartDate(String startDate);

    void showStartTime(String startTime);

    void showElapsedTime(String elapsedTime);

    void endActivity();

    void shareImageIntent(Uri uri);

    Bitmap getMetricsImage();

    void showShareRunError();


}
