package com.itba.runningMate.rundetails;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.itba.runningMate.R;
import com.itba.runningMate.rundetails.model.RunMetricsDetail;

public class RunSummary extends ConstraintLayout {

    private TextView runningTime;
    private TextView speed;
    private TextView pace;
    private TextView distance;
    private TextView calories;

    public RunSummary(Context context) {
        super(context);
        initView(context);
        setUp();
    }

    public RunSummary(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setUp();
    }

    public RunSummary(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        setUp();
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_run_summary, this);
        setBackgroundColor(getResources().getColor(R.color.mate_color));
        setPadding(10, 10, 10, 10);
    }

    private void setUp() {
        speed = findViewById(R.id.speed);
        pace = findViewById(R.id.pace);
        distance = findViewById(R.id.distance);
        runningTime = findViewById(R.id.running_time);
        calories = findViewById(R.id.calories);
    }

    public void bind(RunMetricsDetail detail) {
        speed.setText(detail.getSpeed());
        runningTime.setText(detail.getRunningTime());
        distance.setText(detail.getDistance());
        calories.setText(detail.getCalories());
        pace.setText(detail.getPace());
    }

}
