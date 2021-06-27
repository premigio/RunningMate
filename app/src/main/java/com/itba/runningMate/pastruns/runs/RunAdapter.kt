package com.itba.runningMate.pastruns.runs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.R
import com.itba.runningMate.domain.Run
import java.lang.ref.WeakReference
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

    override fun getItemViewType(position: Int): Int {
        return R.layout.view_run_element
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return RunViewHolder(view)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        if (listener == null) {
            return
        }
        holder.bind(currentRunList[position])
        holder.setOnClickListener(listener!!.get()!!)
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