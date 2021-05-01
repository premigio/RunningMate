package com.itba.runningMate.fragments.pastruns;

import com.itba.runningMate.fragments.history.model.DummyRView;

import java.util.List;

public interface RunListView {

    void updateOldRuns(List<DummyRView> list);
    void showModelToast(DummyRView model);
}
