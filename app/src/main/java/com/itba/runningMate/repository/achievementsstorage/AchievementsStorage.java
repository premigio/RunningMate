package com.itba.runningMate.repository.achievementsstorage;

public interface AchievementsStorage {
    String PREF_FILE_ACHIEVEMENTS = "PREF_FILE_ACHIEVEMENTS";

    void persistState();

    double getTotalDistance();

    void decreaseTotalDistance(double distance);

    void increaseTotalDistance(double distance);
}
