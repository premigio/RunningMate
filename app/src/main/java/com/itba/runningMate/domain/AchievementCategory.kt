package com.itba.runningMate.domain

import com.itba.runningMate.R

enum class AchievementCategory(
    val title: Int
) {

    TIME(R.string.achievement_category_time),
    CALORIES(R.string.achievement_category_calories),
    DISTANCE(R.string.achievement_category_distance),
    SPEED(R.string.achievement_category_speed),
//    STREAKS(R.string.achievement_category_streak),
    ;

    companion object {
        fun getAchievements(category: AchievementCategory): List<Achievements> {
            val list = mutableListOf<Achievements>()
            for (achievement in Achievements.values()) {
                if (achievement.category == category) {
                    list.add(achievement)
                }
            }
            return list
        }
    }
}