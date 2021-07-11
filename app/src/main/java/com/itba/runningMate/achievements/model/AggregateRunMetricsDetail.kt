package com.itba.runningMate.achievements.model

data class AggregateRunMetricsDetail(
    val elapsedTime: Long?,
    val runningTime: Long?,
    val speed: Float?,
    val pace: Long?,
    var distance: Float?, // modified for functionality test
    val calories: Int?
) {

    data class Builder(
        var elapsedTime: Long? = null,
        var runningTime: Long? = null,
        var speed: Float? = null,
        var pace: Long? = null,
        var distance: Float? = null,
        var calories: Int? = null
    ) {

        fun build() = AggregateRunMetricsDetail(
            elapsedTime,
            runningTime,
            speed,
            pace,
            distance,
            calories
        )

        fun elapsedTime(elapsedTime: Long) = apply { this.elapsedTime = elapsedTime }

        fun runningTime(runningTime: Long) = apply { this.runningTime = runningTime }

        fun speed(speed: Float) = apply { this.speed = speed }

        fun pace(pace: Long) = apply { this.pace = pace }

        fun distance(distance: Float) = apply { this.distance = distance }

        fun calories(calories: Int?) = apply { this.calories = calories }

    }

}