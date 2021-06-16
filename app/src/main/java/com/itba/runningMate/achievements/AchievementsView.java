package com.itba.runningMate.achievements;

import com.itba.runningMate.achievements.achievement.Achievements;

public interface AchievementsView {

    void setGoalTitle(int title);

    void setGoalSubtitle(int subtitle);

    void setGoalImage(int image);

    void setProgressBar(double distance, double max);

    void setAchievement(Achievements achievementNumber, boolean achieved);
}
