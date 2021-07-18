package com.itba.runningMate.achievements.achievement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.R
import com.itba.runningMate.domain.AchievementCategory

class AchievementCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var titleTextView: TextView = itemView.findViewById(R.id.achievement_category_title)

    companion object {
        fun from(parent: ViewGroup): AchievementCategoryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_achievement_category_element, parent, false)
            return AchievementCategoryViewHolder(view)
        }
    }

    fun bind(achievementCategory: AchievementCategory) {
        titleTextView.setText(achievementCategory.title)
    }

}