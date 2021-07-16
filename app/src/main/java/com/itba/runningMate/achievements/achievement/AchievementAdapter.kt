package com.itba.runningMate.achievements.achievement

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.domain.Achievements

class AchievementAdapter : RecyclerView.Adapter<AchievementViewHolder>() {

    private var completedAchievements: Array<Achievements>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = AchievementElementView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return AchievementViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Achievements.values().size
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievementAtPosition = Achievements.values()[position]
        val isCompleted = completedAchievements?.contains(achievementAtPosition) ?: false
        holder.bind(
            achievementAtPosition,
            isCompleted
        )
    }

    fun update(completedAchievements: Array<Achievements>) {
        this.completedAchievements = completedAchievements
        notifyDataSetChanged()
    }


}
