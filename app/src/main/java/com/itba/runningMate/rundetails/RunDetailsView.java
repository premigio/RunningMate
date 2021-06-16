package com.itba.runningMate.rundetails;

import android.graphics.Bitmap;
import android.net.Uri;

import com.itba.runningMate.domain.Route;
import com.itba.runningMate.rundetails.model.RunMetricsDetail;

public interface RunDetailsView {

    void showRoute(Route route);

    void showRunMetrics(RunMetricsDetail runMetrics);

    void endActivity();

    void shareImageIntent(Uri uri);

    Bitmap getMetricsImage(RunMetricsDetail detail);

    void showShareRunError();

    void showUpdateTitleError();

    void showDeleteError();

    void showRunNotAvailableError();

}
