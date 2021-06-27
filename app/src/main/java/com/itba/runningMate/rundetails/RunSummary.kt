package com.itba.runningMate.rundetails

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.itba.runningMate.R
import com.itba.runningMate.rundetails.model.RunMetricsDetail

class RunSummary : ConstraintLayout {

    private lateinit var runningTime: TextView
    private lateinit var speed: TextView
    private lateinit var pace: TextView
    private lateinit var distance: TextView
    private lateinit var calories: TextView

    constructor(context: Context) : super(context) {
        initView(context)
        setUp()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
        setUp()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
        setUp()
    }

    private fun initView(context: Context) {
        inflate(context, R.layout.view_run_summary, this)
        setBackgroundColor(resources.getColor(R.color.mate_color))
        setPadding(10, 10, 10, 10)
    }

    private fun setUp() {
        speed = findViewById(R.id.speed)
        pace = findViewById(R.id.pace)
        distance = findViewById(R.id.distance)
        runningTime = findViewById(R.id.running_time)
        calories = findViewById(R.id.calories)
    }

    fun bind(detail: RunMetricsDetail) {
        speed.text = detail.speed
        runningTime.text = detail.runningTime
        distance.text = detail.distance
        calories.text = detail.calories
        pace.text = detail.pace
    }
}