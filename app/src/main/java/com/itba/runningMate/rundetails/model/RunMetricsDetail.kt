package com.itba.runningMate.rundetails.model

import com.itba.runningMate.domain.Run
import com.itba.runningMate.utils.Formatters
import java.util.*

class RunMetricsDetail private constructor() {

    lateinit var runTimeInterval: String
        private set
    lateinit var elapsedTime: String
        private set
    lateinit var runningTime: String
        private set
    lateinit var speed: String
        private set
    lateinit var pace: String
        private set
    lateinit var distance: String
        private set
    lateinit var title: String
        private set
    lateinit var calories: String
        private set

    companion object {
        fun from(run: Run): RunMetricsDetail {
            val detail = RunMetricsDetail()
            detail.speed = Formatters.twoDecimalPlacesFormatter.format(run.velocity!!.toDouble())
            detail.pace = Formatters.paceFormatter.format(Date(run.pace!!))
            detail.distance = Formatters.twoDecimalPlacesFormatter.format(run.distance!!.toDouble())
            detail.runTimeInterval =
                Formatters.datetimeFormat.format(run.startTime!!) + " - " + Formatters.timeFormat.format(
                    run.endTime!!
                )
            detail.elapsedTime = Formatters.hmsTimeFormatter(run.endTime.time - run.startTime.time)
            detail.title = run.title!!
            detail.runningTime = Formatters.hmsTimeFormatter(run.runningTime!!)
            detail.calories = run.calories.toString()
            return detail
        }
    }
}