package com.itba.runningMate.achievements.achievement

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.components.achievement.AchievementElementView
import com.itba.runningMate.domain.Achievements

class AchievementViewHolder(private val achievementView: AchievementElementView) :
    RecyclerView.ViewHolder(achievementView) {

    companion object {
        fun from(parent: ViewGroup): AchievementViewHolder {
            val view = AchievementElementView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return AchievementViewHolder(view)
        }
    }

    fun bind(achievement: Achievements, achieved: Boolean) {
        achievementView.bind(achievement, achieved)
    }

}