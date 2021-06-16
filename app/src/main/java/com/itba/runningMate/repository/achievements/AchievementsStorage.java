package com.itba.runningMate.repository.achievements;

public interface AchievementsStorage {

    String ACHIEVEMENTS_PREFERENCES_FILE = "PREF_RUNNING_MATE_ACHIEVEMENTS";

    void persistState();

    double getTotalDistance();

    void decreaseTotalDistance(double distance);

    void increaseTotalDistance(double distance);

}
