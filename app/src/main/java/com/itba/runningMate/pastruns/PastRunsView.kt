package com.itba.runningMate.pastruns

import com.itba.runningMate.domain.Run

interface PastRunsView {

    fun updatePastRuns(list: List<Run>)

    fun showNoPastRunsMessage()

    fun hideNoPastRunsMessage()

    fun launchRunDetails(id: Long)

    fun showDeleteError()

}