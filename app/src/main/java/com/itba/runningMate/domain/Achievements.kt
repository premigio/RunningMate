package com.itba.runningMate.domain

import com.itba.runningMate.R
import com.itba.runningMate.achievements.model.AggregateRunMetricsDetail
import java.util.*
import kotlin.collections.HashMap

enum class Achievements(
    val title: Int,
    val description: Int,
    val image: Int,
    val category: AchievementCategory
) {

    // Distance achievements

    DISTANCE_8(
        R.string.max_distance_8_achievement_title,
        R.string.max_distance_8_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.DISTANCE
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return (aggregate.distance ?: 0).toFloat() >= 100.0
        }
    },
    DISTANCE_16(
        R.string.max_distance_16_achievement_title,
        R.string.max_distance_16_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.DISTANCE
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return (aggregate.distance ?: 0).toFloat() >= 500.0
        }
    },
    DISTANCE_32(
        R.string.max_distance_32_achievement_title,
        R.string.max_distance_32_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.DISTANCE
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return (aggregate.distance ?: 0).toFloat() >= 1000.0
        }
    },
    DISTANCE_42(
        R.string.max_distance_42_achievement_title,
        R.string.max_distance_42_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.DISTANCE
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return (aggregate.distance ?: 0).toFloat() >= 3000.0
        }
    },

    // Calories achievements

    KCAL_500(
        R.string.max_kcal_500_achievement_title,
        R.string.max_kcal_500_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.CALORIES
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return aggregate.calories ?: 0 >= 1000
        }
    },
    KCAL_1000(
        R.string.max_kcal_1000_achievement_title,
        R.string.max_kcal_1000_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.CALORIES
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return aggregate.calories ?: 0 >= 8000
        }
    },
    KCAL_2000(
        R.string.max_kcal_2000_achievement_title,
        R.string.max_kcal_2000_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.CALORIES
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return aggregate.calories ?: 0 >= 2000
        }
    },
    KCAL_3000(
        R.string.max_kcal_3000_achievement_title,
        R.string.max_kcal_3000_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.CALORIES
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return aggregate.calories ?: 0 >= 5000
        }
    },

    // Speed achievements

    SPEED_8(
        R.string.max_speed_8_achievement_title,
        R.string.max_speed_8_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.SPEED
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return (aggregate.speed ?: 0).toFloat() >= 8.0
        }
    },
    SPEED_10(
        R.string.max_speed_10_achievement_title,
        R.string.max_speed_10_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.SPEED
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return (aggregate.speed ?: 0).toFloat() >= 10.0
        }
    },
    SPEED_14(
        R.string.max_speed_14_achievement_title,
        R.string.max_speed_14_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.SPEED
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return (aggregate.speed ?: 0).toFloat() >= 12.0
        }
    },
    SPEED_16(
        R.string.max_speed_16_achievement_title,
        R.string.max_speed_16_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.SPEED
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return (aggregate.speed ?: 0).toFloat() >= 14.0
        }
    },

    // Time achievements

    TIME_30M(
        R.string.max_time_30m_achievement_title,
        R.string.max_time_30m_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.TIME
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return aggregate.runningTime ?: 0 >= 3600000
        }
    },
    TIME_1H(
        R.string.max_time_1h_achievement_title,
        R.string.max_time_1h_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.TIME
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return aggregate.runningTime ?: 0 >= 180000000
        }
    },
    TIME_1H_30M(
        R.string.max_time_1h_30m_achievement_title,
        R.string.max_time_1h_30m_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.TIME
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return aggregate.runningTime ?: 0 >= 360000000
        }
    },
    TIME_3H(
        R.string.max_time_3h_achievement_title,
        R.string.max_time_3h_achievement_subtitle,
        R.drawable.badge_icon,
        AchievementCategory.TIME
    ) {
        override fun completed(aggregate: AggregateRunMetricsDetail): Boolean {
            return aggregate.runningTime ?: 0 >= 1080000000
        }
    },
    ;

    abstract fun completed(aggregate: AggregateRunMetricsDetail): Boolean

}