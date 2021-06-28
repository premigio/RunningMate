package com.itba.runningMate.domain

import com.google.android.gms.maps.model.LatLng
import java.util.*

data class Run(
    val uid: Long?,
    val route: List<List<LatLng>>?,
    val startTime: Date?,
    val endTime: Date?,
    /* km */
    val distance: Float?,
    /* km/h */
    val velocity: Float?,
    val pace: Long?,
    val runningTime: Long?,
    val calories: Int?,
    val title: String?
) {

    data class Builder(
        var uid: Long? = null,
        var route: List<List<LatLng>>? = null,
        var startTime: Date? = null,
        var endTime: Date? = null,
        /* km */
        var distance: Float? = null,
        /* km/h */
        var velocity: Float? = null,
        var pace: Long? = null,
        var runningTime: Long? = null,
        var calories: Int? = null,
        var title: String? = null
    ) {

        fun build() = Run(
            uid,
            route,
            startTime,
            endTime,
            distance,
            velocity,
            pace,
            runningTime,
            calories,
            title
        )

        fun uid(uid: Long) = apply { this.uid = uid }

        fun route(route: List<List<LatLng>>?) = apply { this.route = route }

        fun startTime(startTime: Date?) = apply { this.startTime = startTime }

        fun endTime(endTime: Date?) = apply { this.endTime = endTime }

        fun distance(distance: Float) = apply { this.distance = distance }

        fun velocity(velocity: Float) = apply { this.velocity = velocity }

        fun pace(pace: Long) = apply { this.pace = pace }

        fun runningTime(runningTime: Long) = apply { this.runningTime = runningTime }

        fun calories(calories: Int) = apply { this.calories = calories }

        fun title(title: String?) = apply { this.title = title }
    }
}