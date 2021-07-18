package com.itba.runningMate.repository.achievements

import android.content.SharedPreferences
import com.itba.runningMate.db.achievement.AchievementDao
import com.itba.runningMate.db.achievement.AchievementEntityBuilder
import com.itba.runningMate.domain.Achievements
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.*

class AchievementsRepositoryImpl(
    private val preferences: SharedPreferences,
    private val achievementDao: AchievementDao
) : AchievementsRepository {

    private var totalDistance: Double

    override fun persistState() {
        val editor = preferences.edit()
        editor.putFloat(KEY_TOTAL_DISTANCE, totalDistance.toFloat())
        editor.apply()
    }

    override fun getTotalDistance(): Double {
        return totalDistance
    }

    override fun decreaseTotalDistance(distance: Double) {
        if (totalDistance - distance < 0.0) totalDistance = 0.0 else totalDistance -= distance
    }

    override fun increaseTotalDistance(distance: Double) {
        totalDistance += distance
    }

    override fun getAchievements(): Flowable<List<Achievements>> {
        return achievementDao.getAchievements()
            .map { l -> l.map { entity -> entity.achievement!! } }
    }

    override fun getAchievements(n: Int): Flowable<List<Achievements>> {
        return achievementDao.getAchievements()
            .limit(n.toLong())
            .map { l -> l.map { entity -> entity.achievement!! } }
    }

    override fun removeAchievements(achievements: Achievements): Completable {
        return achievementDao.deleteAchievements(achievements)
    }

    override fun addAchievements(achievements: Achievements, timestamp: Date): Completable {
        return achievementDao.insertRoute(
            AchievementEntityBuilder()
                .achievement(achievements)
                .timestamp(timestamp)
                .build()
        )
    }

    companion object {
        private const val KEY_TOTAL_DISTANCE = "total_distance"
    }

    init {
        totalDistance = preferences.getFloat(KEY_TOTAL_DISTANCE, 0.0f)
            .toDouble()
    }
}