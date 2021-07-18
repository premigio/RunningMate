package com.itba.runningMate.mainpage.fragments.feed

import com.itba.runningMate.domain.Achievements
import com.itba.runningMate.domain.Level
import com.itba.runningMate.domain.Run

interface FeedView {

    fun setPastRunCardsNoText()

    fun addRunToCard(i: Int, run: Run)

    fun disappearRuns(abs: Int)

    fun disappearNoText()

    fun showAchievements(achievements: List<Achievements>)

    fun showCurrentLevel(level: Level)

    fun launchLevelsActivity()

    fun launchAchievementsActivity()

    fun launchRunDetailActivity(runId: Long)

    fun launchPastRunsActivity()

    fun startLevelShimmerAnimation()

    fun stopLevelShimmerAnimation()

    fun startRecentActivityShimmerAnimation()

    fun stopRecentActivityShimmerAnimation()

}