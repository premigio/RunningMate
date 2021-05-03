package com.itba.runningMate.mainpage.fragments.pastruns.ui;

import com.itba.runningMate.domain.Sprint;

import java.util.List;

public interface RunListView {

    void updateOldRuns(List<Sprint> list);
    void showModelToast(long modelId);

    void callSprintDetails(long id);
}
