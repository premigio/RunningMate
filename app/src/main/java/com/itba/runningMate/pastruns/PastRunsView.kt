package com.itba.runningMate.pastruns;

import com.itba.runningMate.domain.Run;

import java.util.List;

public interface PastRunsView {

    void updatePastRuns(List<Run> list);

    void showNoPastRunsMessage();

    void hideNoPastRunsMessage();

    void launchRunDetails(long id);
}
