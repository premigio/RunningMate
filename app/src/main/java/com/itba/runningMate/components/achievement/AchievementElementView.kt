package com.itba.runningMate.components.achievement

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
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
    private lateinit var bageImageView: ImageView

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
        bageImageView = findViewById(R.id.badge_icon)
//        badgeBckgroundImageView = findViewById(R.id.badge_icon_background)
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
        bageImageView.setImageResource(achievement.image)
        if (achieved) {
            bageImageView.alpha = 1.0f
        } else {
            bageImageView.alpha = 0.3f
        }
        setBadgeColor(achievement.level)
    }

    private fun setBadgeColor(level: AchievementLevel) {
        when (level) {
            AchievementLevel.BRONZE -> {
                bageImageView.background.setColorFilter(
                    Color.parseColor("#CD7F32"),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
            AchievementLevel.SILVER -> {
                bageImageView.background.setColorFilter(
                    Color.parseColor("#C0C0C0"),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
            AchievementLevel.GOLD -> {
                bageImageView.background.setColorFilter(
                    Color.parseColor("#FFD700"),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
            AchievementLevel.PLATINUM -> {
                bageImageView.background.setColorFilter(
                    resources.getColor(R.color.mate_color),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }
    }
}