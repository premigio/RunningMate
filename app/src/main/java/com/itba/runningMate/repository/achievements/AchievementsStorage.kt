package com.itba.runningMate.repository.achievements

interface AchievementsStorage {

    fun persistState()

    fun getTotalDistance(): Double

    fun decreaseTotalDistance(distance: Double)

    fun increaseTotalDistance(distance: Double)

    companion object {
        const val ACHIEVEMENTS_PREFERENCES_FILE = "PREF_RUNNING_MATE_ACHIEVEMENTS"
    }
}