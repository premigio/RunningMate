package com.itba.runningMate.achievements.achievement

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.itba.runningMate.R
import com.itba.runningMate.domain.AchievementLevel
import com.itba.runningMate.domain.Achievements

class AchievementElementView : ConstraintLayout {

    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var imageView: ImageView

    constructor(context: Context) : super(context) {
        inflate(context, R.layout.view_achievement_element, this)
        setUp()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inflate(context, R.layout.view_achievement_element, this)
        setUp()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        inflate(context, R.layout.view_achievement_element, this)
        setUp()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        inflate(context, R.layout.view_achievement_element, this)
        setUp()
    }

    private fun setUp() {
        imageView = findViewById(R.id.badge_icon)
        titleTextView = findViewById(R.id.achievement_title_name)
        descriptionTextView = findViewById(R.id.achievement_description)
    }

    fun bind(achievement: Achievements) {
        titleTextView.setText(achievement.title)
        descriptionTextView.setText(achievement.description)
    }

    fun bind(achievement: Achievements, achieved: Boolean) {
        titleTextView.setText(achievement.title)
        descriptionTextView.setText(achievement.description)
        setBadgeColor(achievement.level)
        if (achieved) {
            imageView.alpha = 1.0f
        }
    }

    private fun setBadgeColor(level: AchievementLevel) {
        when (level) {
            AchievementLevel.BRONZE -> {
                imageView.background.setTint(Color.parseColor("#b08d57"))
            }
            AchievementLevel.SILVER -> {
                imageView.background.setTint(Color.parseColor("#aaa9ad"))
            }
            AchievementLevel.GOLD -> {
                imageView.background.setTint(Color.parseColor("#d4af37"))
            }
            AchievementLevel.PLATINUM -> {
                imageView.background.setTint(Color.parseColor("#e5e4e2"))
            }
        }
    }
}