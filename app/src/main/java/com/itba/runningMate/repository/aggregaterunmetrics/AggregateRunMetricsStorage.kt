package com.itba.runningMate.repository.aggregaterunmetrics

import com.itba.runningMate.domain.AggregateRunMetricsDetail

interface AggregateRunMetricsStorage {

    fun getTotalDistance(): Double

    fun incrementTotalDistance(distance: Double)

    fun decreaseTotalDistance(distance: Double)

    fun getAggregateRunMetricsDetail(): AggregateRunMetricsDetail

    fun getMaxDistance(): Double

    fun getMaxRunningTime(): Long

    fun getMaxSpeed(): Float

    fun getMaxPace(): Long

    fun getMaxCalories(): Int

    fun updateMaxDistance(distance: Double)

    fun updateMaxRunningTime(runningTime: Long)

    fun updateMaxSpeed(speed: Float)

    fun updateMaxPace(pace: Long)

    fun updateMaxCalories(calories: Int)

    fun persistState()

    companion object {
        const val AGGREGATE_RUN_METRICS_PREFERENCES_FILE = "PREF_RUNNING_MATE_AGGREGATE_RUN_METRICS"
    }
}