package com.itba.runningMate.levels.level

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.R
import com.itba.runningMate.domain.Level

class LevelAdapter : RecyclerView.Adapter<LevelViewHolder>() {

    private var currentLevel: Level? = null

    override fun getItemViewType(position: Int): Int {
        return R.layout.activity_levels_view_level
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return LevelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Level.values().size
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        holder.bind(
            Level.values()[position],
            (currentLevel != null && position <= currentLevel!!.ordinal)
        )
    }

    fun update(currentLevel: Level) {
        this.currentLevel = currentLevel
        notifyDataSetChanged()
    }

}