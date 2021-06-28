package com.itba.runningMate.mainpage.fragments.feed

import com.itba.runningMate.domain.Run

interface FeedView {

    fun setPastRunCardsNoText()

    fun addRunToCard(i: Int, run: Run)

    fun disappearRuns(abs: Int)

    fun disappearNoText()

    fun setGoalTitle(title: Int)

    fun setGoalSubtitle(subtitle: Int)

    fun setGoalImage(image: Int)

    fun launchAchievementsActivity()

    fun launchRunDetailActivity(runId: Long)

    fun launchPastRunsActivity()

}