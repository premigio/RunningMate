package com.itba.runningMate.db.achievement

import com.itba.runningMate.domain.Achievements
import java.util.*

data class AchievementEntityBuilder(
    var achievement: Achievements? = null,
    var timestamp: Date? = null
) {

    fun build() = AchievementEntity(achievement!!, timestamp!!)

    fun achievement(achievement: Achievements) = apply { this.achievement = achievement }

    fun timestamp(timestamp: Date) = apply { this.timestamp = timestamp }
}
