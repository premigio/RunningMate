package com.itba.runningMate.levels

import com.itba.runningMate.domain.Level

interface LevelsView {

    fun showCurrentLevel(level: Level, distance: Double)

}