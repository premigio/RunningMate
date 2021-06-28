package com.itba.runningMate.mainpage.fragments.feed.run

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.itba.runningMate.R
import com.itba.runningMate.domain.Run
import com.itba.runningMate.utils.Formatters.timeFormat
import java.lang.ref.WeakReference

class RunElementView : FrameLayout {

    private lateinit var title: TextView
    private lateinit var distance: TextView
    private lateinit var time: TextView
    private var ID: Long = 0
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
        title = findViewById(R.id.run_list_card_title)
        distance = findViewById(R.id.run_list_distance_content)
        time = findViewById(R.id.run_list_time_run)
    }

    fun bind(model: Run?) {
        if (model == null) return
        ID = model.uid!!
        title.text = model.title
        distance.text = context.getString(R.string.distance_string, model.distance)
        time.setText(timeFormat.format(model.startTime))
        val cl: ConstraintLayout = findViewById(R.id.old_run_row_ll)
        if (listener.get() != null) cl.setOnClickListener { l: View? ->
            listener.get()!!
                .onRunClick(ID)
        }
    }
}