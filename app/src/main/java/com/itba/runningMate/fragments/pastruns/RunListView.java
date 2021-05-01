package com.itba.runningMate.fragments.pastruns;

import java.util.List;

public interface RunListView {

    void updateOldRuns(List<DummyRView> list);
    void showModelToast(DummyRView model);
}
