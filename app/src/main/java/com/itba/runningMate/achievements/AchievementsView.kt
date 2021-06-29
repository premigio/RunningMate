package com.itba.runningMate.achievements

import com.itba.runningMate.achievements.achievement.Achievements

interface AchievementsView {

    fun setGoalTitle(title: Int)

    fun setGoalSubtitle(subtitle: Int)

    fun setGoalImage(image: Int)

    fun setProgressBar(distance: Double, max: Double)

    fun setAchievement(achievementNumber: Achievements, achieved: Boolean)

}