package com.itba.runningMate.fragments.pastruns;

import com.itba.runningMate.domain.Sprint;

import java.util.List;

public interface RunListView {

    void updateOldRuns(List<Sprint> list);
    void showModelToast(long modelId);
}
