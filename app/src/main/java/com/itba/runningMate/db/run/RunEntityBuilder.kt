package com.itba.runningMate.db.run

import com.google.android.gms.maps.model.LatLng
import java.util.*

data class RunEntityBuilder(
    var uid: Long? = null,
    var route: List<List<LatLng>>? = null,
    var startTime: Date? = null,
    var endTime: Date? = null,
    var elapsedTime: Long? = null,
    var distance: Float? = null,
    var velocity: Float? = null,
    var pace: Long? = null,
    var calories: Int? = null,
    var title: String? = null
) {

    fun build() = RunEntity(
        uid,
        route,
        startTime,
        endTime,
        elapsedTime,
        distance,
        velocity,
        pace,
        calories,
        title
    )

    fun uid(uid: Long?) = apply { this.uid = uid }

    fun route(route: List<List<LatLng>>?) = apply { this.route = route }

    fun startTime(startTime: Date?) = apply { this.startTime = startTime }

    fun endTime(endTime: Date?) = apply { this.endTime = endTime }

    fun elapsedTime(elapsedTime: Long?) = apply { this.elapsedTime = elapsedTime }

    fun distance(distance: Float?) = apply { this.distance = distance }

    fun velocity(velocity: Float?) = apply { this.velocity = velocity }

    fun pace(pace: Long?) = apply { this.pace = pace }

    fun calories(calories: Int?) = apply { this.calories = calories }

    fun title(title: String?) = apply { this.title = title }

}
