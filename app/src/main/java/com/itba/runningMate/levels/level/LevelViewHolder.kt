package com.itba.runningMate.levels.level

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.R
import com.itba.runningMate.domain.Level

class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var titleTextView: TextView = itemView.findViewById(R.id.level_title)
    private var descriptionTextView: TextView = itemView.findViewById(R.id.level_subtitle)
    private var imageView: ImageView = itemView.findViewById(R.id.level_image)
    private var levelUnlockTextView: TextView = itemView.findViewById(R.id.level_unlock_km)

    @SuppressLint("UseCompatLoadingForDrawables")
    fun bind(level: Level, achieved: Boolean) {
        titleTextView.text = itemView.resources.getText(level.title)
        descriptionTextView.text = itemView.resources.getText(level.subTitle)
        imageView.setImageResource(level.image)
        when (level) {
            Level.TARAGUI -> {
                levelUnlockTextView.text = itemView.resources.getString(R.string.empty)
            }
            else -> {
                levelUnlockTextView.text = itemView.resources
                    .getString(R.string.level_unlock_km, level.minKm)
            }
        }
        if (achieved) {
            imageView.alpha = 1.0f
            when (level) {
                Level.TARAGUI -> {
                    levelUnlockTextView.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                }
                else -> {
                    levelUnlockTextView.setCompoundDrawablesWithIntrinsicBounds(
                        imageView.resources.getDrawable(R.drawable.ic_unlock),
                        null,
                        null,
                        null
                    )
                    levelUnlockTextView.text = itemView.resources
                        .getString(R.string.level_unlock_km, level.minKm)
                }
            }
        }
    }

}