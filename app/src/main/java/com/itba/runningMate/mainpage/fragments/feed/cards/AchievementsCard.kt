package com.itba.runningMate.mainpage.fragments.feed.cards

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.itba.runningMate.R
import com.itba.runningMate.achievements.achievement.AchievementElementView
import com.itba.runningMate.domain.Achievements
import com.itba.runningMate.mainpage.fragments.feed.cards.listeners.OnSeeAllAchievementsListener
import java.lang.ref.WeakReference

class AchievementsCard : CardView {


    private lateinit var emptyText: TextView
    private lateinit var achievementElements: List<AchievementElementView>
    private lateinit var seeAll: Button
    private var onSeeAllClickListener: WeakReference<OnSeeAllAchievementsListener>? = null

    constructor(context: Context) : super(context) {
        prepareFromConstructor(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        prepareFromConstructor(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        prepareFromConstructor(context)
    }

    private fun prepareFromConstructor(context: Context) {
        inflate(context, R.layout.card_achievements, this)
        achievementElements = listOf(
            findViewById(R.id.achievement_1),
            findViewById(R.id.achievement_2),
            findViewById(R.id.achievement_3)
        )
        emptyText = findViewById(R.id.card_achievements_empty_textview)
        seeAll = findViewById(R.id.card_achievements_see_all_btn)
        seeAll.setOnClickListener { onSeeAllButtonClicked() }
    }

    private fun onSeeAllButtonClicked() {
        if (onSeeAllClickListener!!.get() != null) {
            onSeeAllClickListener!!.get()?.onSeeAllAchievementsClick()
        }
    }

    fun setSeeAllListener(onSeeAllAchievementsListener: OnSeeAllAchievementsListener?) {
        this.onSeeAllClickListener = WeakReference(onSeeAllAchievementsListener)
    }

    fun bind(achievements: List<Achievements>) {
        if (achievements.isEmpty()) {
            emptyText.visibility = VISIBLE
        }
        for (i in 0..2) {
            if (i < achievements.size) {
                achievementElements[i].bind(
                    resources.getString(achievements[i].title),
                    resources.getString(achievements[i].description),
                    true
                )
            } else {
                achievementElements[i].visibility = GONE
            }
        }
    }

}