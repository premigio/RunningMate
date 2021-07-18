package com.itba.runningMate.achievements.achievement

import com.itba.runningMate.domain.AchievementCategory
import com.itba.runningMate.domain.Achievements

sealed class Item {

    data class AchievementItem(val achievement: Achievements) : Item()

    data class AchievementHeader(val category: AchievementCategory) : Item()

}
