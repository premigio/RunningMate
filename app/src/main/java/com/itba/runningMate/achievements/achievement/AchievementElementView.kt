package com.itba.runningMate.achievements.achievement

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.itba.runningMate.R

class AchievementElementView : FrameLayout {

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

    fun bind(title: String, description: String) {
        titleTextView.text = title
        descriptionTextView.text = description
    }

    fun bind(title: String, description: String, achieved: Boolean) {
        titleTextView.text = title
        descriptionTextView.text = description
        setBadgeVisibility(achieved)
    }

    fun setBadgeVisibility(achieved: Boolean) {
        if (achieved) {
            imageView.alpha = 1.0f
        }
    }
}