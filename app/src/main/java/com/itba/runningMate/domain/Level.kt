package com.itba.runningMate.domain

import com.itba.runningMate.R

enum class Level(
    val title: Int,
    val subTitle: Int,
    val image: Int,
    val minKm: Double,
    val sizeKm: Double
) {

    TARAGUI(R.string.taragui, R.string.taragui_subtitle, R.drawable.taragui, 0.0, 100.0),
    CBSE(R.string.cbse, R.string.cbse_subtitle, R.drawable.cbse, 100.0, 100.0),
    CRUZ_DE_MALTA(
        R.string.cruz_de_malta,
        R.string.cruz_de_malta_subtitle,
        R.drawable.cruzdemalta,
        200.0,
        100.0
    ),
    PLAYADITO(R.string.playadito, R.string.playadito_subtitle, R.drawable.playadito, 300.0, 200.0),
    ROSAMONTE(R.string.rosamonte, R.string.rosamonte_subtitle, R.drawable.rosamonte, 500.0, 250.0),
    LA_MERCED(R.string.merced, R.string.merced_subtitle, R.drawable.lamerced, 750.0, 500.0),
    ;

    companion object {
        fun from(distance: Double): Level {
            var currentLevel = TARAGUI
            for (level in values()) {
                if (distance > level.minKm) {
                    currentLevel = level
                } else {
                    break
                }
            }
            return currentLevel
        }
    }

}