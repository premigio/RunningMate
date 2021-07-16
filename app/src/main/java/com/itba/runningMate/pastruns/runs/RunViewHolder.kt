package com.itba.runningMate.pastruns.runs

import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.domain.Run
import com.itba.runningMate.components.run.OnRunClickListener
import com.itba.runningMate.components.run.RunElementView

class RunViewHolder(private val runElementView: RunElementView) :
    RecyclerView.ViewHolder(runElementView) {

    fun bind(model: Run?) {
        if (model == null) return
        runElementView.bind(model)
    }

    fun setOnClickListener(listener: OnRunClickListener) {
        runElementView.setOnClick(listener)
    }

}