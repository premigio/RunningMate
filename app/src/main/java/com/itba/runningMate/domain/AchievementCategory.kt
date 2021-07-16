package com.itba.runningMate.domain

import com.itba.runningMate.R

enum class AchievementCategory(
    val title: Int
) {

    TIME(R.string.achievement_category_time),
    CALORIES(R.string.achievement_category_calories),
    DISTANCE(R.string.achievement_category_distance),
    SPEED(R.string.achievement_category_speed),
    STREAKS(R.string.achievement_category_streak),
    ;
}