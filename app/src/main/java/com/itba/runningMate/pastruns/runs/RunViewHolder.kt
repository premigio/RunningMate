package com.itba.runningMate.pastruns.runs

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.R
import com.itba.runningMate.domain.Run
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val title: TextView
    private val distance: TextView
    private val time: TextView
    private var id = RecyclerView.NO_ID

    private var listener: WeakReference<OnRunClickListener>? = null

    fun bind(model: Run?) {
        if (model == null) return
        id = model.uid!!
        title.text = model.title
        distance.text = itemView.context.getString(R.string.distance_string, model.distance)
        time.text = timeFormat.format(model.startTime)
    }

    override fun onClick(v: View) {
        //todo: hacer que se lea el getItemId() del adapter
        if (listener == null) {
            return
        }
        listener!!.get()!!.onRunClick(id)
    }

    fun setOnClickListener(listener: OnRunClickListener) {
        this.listener = WeakReference(listener)
    }

    companion object {
        private val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
    }

    init {
        title = itemView.findViewById(R.id.run_list_card_title)
        distance = itemView.findViewById(R.id.run_list_distance_content)
        time = itemView.findViewById(R.id.run_list_time_run)
        itemView.setOnClickListener(this)
    }
}