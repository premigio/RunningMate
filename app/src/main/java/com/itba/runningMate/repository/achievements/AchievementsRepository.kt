package com.itba.runningMate.repository.achievements

import com.itba.runningMate.domain.Achievements
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.*

interface AchievementsRepository {

    fun persistState()

    fun getTotalDistance(): Double

    fun decreaseTotalDistance(distance: Double)

    fun increaseTotalDistance(distance: Double)

    /**
     * Returns list of completed achievements in the order they where achieved
     */
    fun getAchievements(): Flowable<List<Achievements>>

    /**
     * Returns latest n completed achievements
     */
    fun getAchievements(n: Int): Flowable<List<Achievements>>

    /**
     * Removes an achievement from achievements list
     */
    fun removeAchievements(achievements: Achievements): Completable

    /**
     * Adds an achievement to achievements list
     */
    fun addAchievements(achievements: Achievements, timestamp: Date): Completable

    companion object {
        const val ACHIEVEMENTS_PREFERENCES_FILE = "PREF_RUNNING_MATE_ACHIEVEMENTS"
    }
}