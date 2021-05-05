package com.itba.runningMate.mainpage.fragments.pastruns.ui;

import com.itba.runningMate.domain.Run;

import java.util.List;

public interface PastRunsView {

    void updateOldRuns(List<Run> list);

    void showModelToast(long modelId);

    void launchRunDetails(long id);
}
