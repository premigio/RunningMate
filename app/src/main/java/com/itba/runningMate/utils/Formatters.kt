package com.itba.runningMate.utils

import android.annotation.SuppressLint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Formatters {

    val paceFormatter = SimpleDateFormat("mm'' ss'\"'", Locale.getDefault())
    val twoDecimalPlacesFormatter = DecimalFormat("0.00")
    val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

    @JvmField
    val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
    val datetimeFormat = SimpleDateFormat("d/M/y HH:mm", Locale.getDefault())

    @SuppressLint("DefaultLocale")
    fun hmsTimeFormatter(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        return if (hours == 0L) {
            String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(millis)
                ),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millis)
                )
            )
        } else {
            String.format(
                "%02d:%02d:%02d",
                hours,
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(millis)
                ),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millis)
                )
            )
        }
    }
}