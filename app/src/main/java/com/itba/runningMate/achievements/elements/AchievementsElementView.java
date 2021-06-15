package com.itba.runningMate.achievements.elements;

public interface AchievementsElementView {
    void bind(String title, String description);
    void setBadgeVisibility(boolean achieved);
}
