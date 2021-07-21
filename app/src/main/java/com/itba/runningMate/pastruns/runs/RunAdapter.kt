package com.itba.runningMate.pastruns.runs

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.components.run.OnRunClickListener
import com.itba.runningMate.components.run.RunElementView
import com.itba.runningMate.domain.Run
import java.lang.ref.WeakReference
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunViewHolder>() {

    private val currentRunList: MutableList<Run>
    private var clickListener: WeakReference<OnRunClickListener>? = null
    private var deleteListener: WeakReference<OnRunDeleteListener>? = null

    fun update(runList: List<Run>?) {
        currentRunList.clear()
        if (runList != null) {
            currentRunList.addAll(runList)
        }
        notifyDataSetChanged()
    }

    fun setClickListener(listener: OnRunClickListener) {
        this.clickListener = WeakReference(listener)
    }

    fun setSwipeRunToDeleteListener(listener: OnRunDeleteListener) {
        this.deleteListener = WeakReference(listener)
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
        if (clickListener == null) {
            return
        }
        holder.setOnClickListener(clickListener!!.get()!!)
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

    fun deleteItem(position: Int) {
        val runId = currentRunList[position].uid!!
        currentRunList.removeAt(position)
        notifyDataSetChanged()
        if (deleteListener?.get() != null) {
            deleteListener!!.get()!!.onSwipeRunToDelete(runId)
        }
    }

    init {
        currentRunList = ArrayList()
    }
}