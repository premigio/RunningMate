package com.itba.runningMate.achievements.achievement

interface AchievementsElementView {

    fun bind(title: String, description: String)

    fun setBadgeVisibility(achieved: Boolean)
}