package com.itba.runningMate.rundetails;

import com.itba.runningMate.domain.Run;

public interface RunDetailsView {
    void bindRunDetails(Run run);

    void endActivity();

}
