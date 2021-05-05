package com.itba.runningMate.rundetails;

import com.itba.runningMate.domain.Run;

public interface RunDetailsView {

    void bindRunMetrics(Run run);

    void bindRunRoute(Run run);

    void endActivity();

}
