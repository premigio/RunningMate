package com.itba.runningMate.components.run

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.itba.runningMate.R
import com.itba.runningMate.domain.Run
import com.itba.runningMate.utils.Formatters.timeFormat
import java.lang.ref.WeakReference

class RunElementView : ConstraintLayout {

    private lateinit var title: TextView
    private lateinit var distance: TextView
    private lateinit var time: TextView
    private var id: Long = 0
    private lateinit var listener: WeakReference<OnRunClickListener>

    constructor(context: Context?) : super(context!!) {
        inflate(context, R.layout.view_run_element, this)
        setUp()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        inflate(context, R.layout.view_run_element, this)
        setUp()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        inflate(context, R.layout.view_run_element, this)
        setUp()
    }

    fun setOnClick(listener: OnRunClickListener?) {
        this.listener = WeakReference(listener)
    }

    private fun setUp() {
        title = findViewById(R.id.run_title)
        distance = findViewById(R.id.run_distance)
        time = findViewById(R.id.run_time)
    }

    fun bind(model: Run?) {
        if (model == null) return
        id = model.uid!!
        title.text = model.title
        distance.text = context.getString(R.string.distance_string, model.distance)
        time.text = timeFormat.format(model.startTime)
        if (listener.get() != null) {
            this.setOnClickListener { listener.get()!!.onRunClick(id) }
        }
    }
}