package com.itba.runningMate.rundetails;

import android.graphics.Bitmap;
import android.net.Uri;

import com.itba.runningMate.domain.Run;

public interface RunDetailsView {

    void bindRunMetrics(Run run);

    void bindRunRoute(Run run);

    void endActivity();

    void shareImageIntent(Uri uri, Bitmap bitmap);

    Bitmap getMetricsImage();

    void showShareRunError();


}
