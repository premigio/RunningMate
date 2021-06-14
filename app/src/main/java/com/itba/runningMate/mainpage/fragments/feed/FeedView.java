package com.itba.runningMate.mainpage.fragments.feed;

import com.itba.runningMate.domain.Run;

public interface FeedView {

    void goToPastRunsActivity();

    void setPastRunCardsNoText();

    void addRunToCard(int i, Run run);

    void disappearRuns(int abs);

    void disappearNoText();

    void setGoalTitle(int title);

    void setGoalSubtitle(int subtitle);

    void setGoalImage(int image);
}
