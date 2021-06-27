package com.itba.runningMate.repository.achievements

import android.content.SharedPreferences

class AchievementsStorageImpl(private val preferences: SharedPreferences) : AchievementsStorage {

    private var totalDistance: Double

    override fun persistState() {
        val editor = preferences.edit()
        if (totalDistance != null) {
            editor.putFloat(KEY_TOTAL_DISTANCE, totalDistance.toFloat())
        }
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

    companion object {
        private const val KEY_TOTAL_DISTANCE = "total_distance"
    }

    init {
        totalDistance = preferences.getFloat(KEY_TOTAL_DISTANCE, 0.0f)
            .toDouble()
    }
}