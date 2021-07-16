package com.itba.runningMate.domain

import com.itba.runningMate.R
import com.itba.runningMate.achievements.model.AggregateRunMetricsDetail

enum class Achievements(
    val title: Int,
    val description: Int,
    val image: Int,
    val category: AchievementCategory
) {

    DISTANCE2000(
        R.string.total_distance_achievement_title,
        R.string.total_distance_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.DISTANCE
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return (aggregate.distance ?: 0).toFloat() >= 2000.0
        }
    },
    KCAL1000(
        R.string.max_kcal_achievement_title,
        R.string.max_kcal_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.CALORIES
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return aggregate.calories ?: 0 >= 1000
        }
    },
    SPEED10(
        R.string.max_speed_achievement_title,
        R.string.max_speed_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.SPEED
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return (aggregate.speed ?: 0).toFloat() >= 10.0
        }
    },
    TIME1H(
        R.string.max_time_achievement_title,
        R.string.max_time_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.TIME
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return aggregate.runningTime ?: 0 >= 3600000
        }
    },
    ;

    abstract fun completed(aggregate: AggregateRunMetricsDetail): Boolean

}