package com.itba.runningMate.achievements

import com.itba.runningMate.achievements.achievement.Achievements

interface AchievementsView {

    fun setAchievement(achievementNumber: Achievements, achieved: Boolean)

}