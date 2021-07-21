package com.itba.runningMate.achievements.achievement

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.domain.AchievementCategory
import com.itba.runningMate.domain.Achievements

class AchievementAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1

    private var completedAchievements: List<Achievements>? = null
    private val flatItems: MutableList<Item> = mutableListOf()

    init {
        for (category in AchievementCategory.values()) {
            flatItems.add(Item.AchievementHeader(category))
            AchievementCategory.getAchievements(category)
                .forEach { a -> flatItems.add(Item.AchievementItem(a)) }
        }
        print(flatItems)
    }

    override fun getItemViewType(position: Int): Int {
        return when (flatItems[position]) {
            is Item.AchievementItem -> ITEM_VIEW_TYPE_ITEM
            is Item.AchievementHeader -> ITEM_VIEW_TYPE_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> AchievementCategoryViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> AchievementViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemCount(): Int {
        return flatItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AchievementViewHolder -> {
                val achievement = (flatItems[position] as Item.AchievementItem).achievement
                val isCompleted = completedAchievements?.contains(achievement) ?: false
                holder.bind(achievement, isCompleted)
            }
            is AchievementCategoryViewHolder -> {
                val category = (flatItems[position] as Item.AchievementHeader).category
                holder.bind(category)
            }
        }
    }

    fun update(completedAchievements: List<Achievements>) {
        this.completedAchievements = completedAchievements
        notifyDataSetChanged()
    }

}
