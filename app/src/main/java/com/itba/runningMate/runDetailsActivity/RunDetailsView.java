package com.itba.runningMate.runDetailsActivity;

import com.itba.runningMate.domain.Sprint;

public interface RunDetailsView {
    void bindRunDetails(Sprint sprint);

    void endActivity();

}
