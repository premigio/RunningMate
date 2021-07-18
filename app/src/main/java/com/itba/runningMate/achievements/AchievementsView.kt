package com.itba.runningMate.achievements

import com.itba.runningMate.domain.Achievements

interface AchievementsView {

    fun showAchievements(achievements: Array<Achievements>)

}