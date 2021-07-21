package com.itba.runningMate.repository.achievements

import com.itba.runningMate.domain.Achievements
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.*

interface AchievementsRepository {

    /**
     * Returns list of completed achievements in the order they where achieved
     */
    fun getAchievements(): Flowable<List<Achievements>>

    /**
     * Returns latest n completed achievements
     * @param n number of achievements to retrive
     */
    fun getAchievements(n: Int): Flowable<List<Achievements>>

    /**
     * Removes an achievement from achievements list
     * @param achievements achievement to delete
     */
    fun removeAchievements(achievements: Achievements): Completable

    /**
     * Adds an achievement to achievements list
     * @param achievements achievement to add
     * @param timestamp date when achievement was latest accomplished
     */
    fun addAchievement(achievements: Achievements, timestamp: Date): Completable

    /**
     * Adds an achievements to achievements list, if achievement already completed it will be discarded
     * @param achievements achievements to add
     * @param timestamp date when achievements were latest accomplished
     */
    fun addAchievements(achievements: List<Achievements>, timestamp: Date): Completable

}