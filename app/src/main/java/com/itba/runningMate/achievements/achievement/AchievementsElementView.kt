package com.itba.runningMate.achievements.achievement;

public interface AchievementsElementView {

    void bind(String title, String description);

    void setBadgeVisibility(boolean achieved);

}
