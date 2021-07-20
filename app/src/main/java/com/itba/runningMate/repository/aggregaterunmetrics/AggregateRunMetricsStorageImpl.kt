package com.itba.runningMate.repository.aggregaterunmetrics

import android.content.SharedPreferences
import com.itba.runningMate.domain.AggregateRunMetricsDetail
import kotlin.math.max

class AggregateRunMetricsStorageImpl(private val preferences: SharedPreferences) :
    AggregateRunMetricsStorage {

    private var totalDistance: Double = preferences.getFloat(KEY_TOTAL_DISTANCE, 0.0F).toDouble()
    private var maxDistance: Double = preferences.getFloat(KEY_MAX_DISTANCE, 0.0F).toDouble()
    private var maxRunningTime: Long = preferences.getLong(KEY_MAX_RUNNING_TIME, 0L)
    private var maxSpeed: Float = preferences.getFloat(KEY_MAX_SPEED, 0.0F)
    private var maxPace: Long = preferences.getLong(KEY_MAX_PACE, 0L)
    private var maxCalories: Int = preferences.getInt(KEY_MAX_CALORIES, 0)

    override fun getTotalDistance(): Double {
        return totalDistance
    }

    override fun incrementTotalDistance(distance: Double) {
        totalDistance += distance
    }

    override fun decreaseTotalDistance(distance: Double) {
        if (totalDistance - distance < 0.0) totalDistance = 0.0 else totalDistance -= distance
    }

    override fun getAggregateRunMetricsDetail(): AggregateRunMetricsDetail {
        return AggregateRunMetricsDetail.Builder()
            .runningTime(maxRunningTime)
            .speed(maxSpeed)
            .pace(maxPace)
            .distance(maxDistance.toFloat())
            .calories(maxCalories)
            .build()
    }

    override fun getMaxDistance(): Double {
        return maxDistance
    }

    override fun getMaxRunningTime(): Long {
        return maxRunningTime
    }

    override fun getMaxSpeed(): Float {
        return maxSpeed
    }

    override fun getMaxPace(): Long {
        return maxPace
    }

    override fun getMaxCalories(): Int {
        return maxCalories
    }

    override fun updateMaxDistance(distance: Double) {
        maxDistance = max(maxDistance, distance)
    }

    override fun updateMaxRunningTime(runningTime: Long) {
        maxRunningTime = max(maxRunningTime, runningTime)
    }

    override fun updateMaxSpeed(speed: Float) {
        maxSpeed = max(maxSpeed, speed)
    }

    override fun updateMaxPace(pace: Long) {
        maxPace = max(maxPace, pace)
    }

    override fun updateMaxCalories(calories: Int) {
        maxCalories = max(maxCalories, calories)
    }

    override fun persistState() {
        val editor = preferences.edit()
        editor.putFloat(KEY_TOTAL_DISTANCE, totalDistance.toFloat())
        editor.putFloat(KEY_MAX_DISTANCE, maxDistance.toFloat())
        editor.putLong(KEY_MAX_RUNNING_TIME, maxRunningTime)
        editor.putFloat(KEY_MAX_SPEED, maxSpeed)
        editor.putLong(KEY_MAX_PACE, maxPace)
        editor.putInt(KEY_MAX_CALORIES, maxCalories)
        editor.apply()
    }

    companion object {
        private const val KEY_TOTAL_DISTANCE = "total_distance"
        private const val KEY_MAX_DISTANCE = "max_distance"
        private const val KEY_MAX_RUNNING_TIME = "max_running_time"
        private const val KEY_MAX_SPEED = "max_speed"
        private const val KEY_MAX_PACE = "max_pace"
        private const val KEY_MAX_CALORIES = "max_calories"
    }

}