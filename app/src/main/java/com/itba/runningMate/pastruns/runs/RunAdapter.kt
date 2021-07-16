package com.itba.runningMate.pastruns.runs

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.domain.Run
import com.itba.runningMate.mainpage.fragments.feed.run.RunElementView
import java.lang.ref.WeakReference
import com.itba.runningMate.mainpage.fragments.feed.run.OnRunClickListener
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunViewHolder>() {

    private val currentRunList: MutableList<Run>
    private var listener: WeakReference<OnRunClickListener>? = null

    fun update(runList: List<Run>?) {
        currentRunList.clear()
        if (runList != null) {
            currentRunList.addAll(runList)
        }
        notifyDataSetChanged()
    }

    fun setClickListener(listener: OnRunClickListener) {
        this.listener = WeakReference(listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val view = RunElementView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return RunViewHolder(view)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        if (listener == null) {
            return
        }
        holder.setOnClickListener(listener!!.get()!!)
        holder.bind(currentRunList[position])
    }

    override fun getItemId(position: Int): Long {
        if (currentRunList.isEmpty()) {
            return RecyclerView.NO_ID
        }
        val pos = currentRunList[position]
        return pos.uid ?: RecyclerView.NO_ID
    }

    override fun getItemCount(): Int {
        return currentRunList.size
    }

    init {
        currentRunList = ArrayList()
    }
}