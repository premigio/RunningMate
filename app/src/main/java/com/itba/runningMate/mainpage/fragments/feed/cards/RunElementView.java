package com.itba.runningMate.mainpage.fragments.feed.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.itba.runningMate.R;
import com.itba.runningMate.domain.Run;

import java.lang.ref.WeakReference;

import static com.itba.runningMate.utils.Formatters.timeFormat;

public class RunElementView extends FrameLayout {

    private TextView title, distance, time;

    private long ID;

    private WeakReference<OnRunClickListener> listener;

    public RunElementView(Context context) {
        super(context);
        inflate(context, R.layout.run_element, this);
        setUp();
    }

    public RunElementView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.run_element, this);
        setUp();
    }

    public RunElementView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.run_element, this);
        setUp();
    }

    public void setOnClick(OnRunClickListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    private void setUp() {
        title = findViewById(R.id.run_list_card_title);
        distance = findViewById(R.id.run_list_distance_content);
        time = findViewById(R.id.run_list_time_run);
    }

    public void bind(Run model) {
        if (model == null) return;

        ID = model.getUid();
        title.setText(model.getTitle());
        distance.setText(getContext().getString(R.string.distance_string, model.getDistance()));
        time.setText(timeFormat.format(model.getStartTime()));

        ConstraintLayout cl = this.findViewById(R.id.old_run_row_ll);
        if (listener.get() != null)
            cl.setOnClickListener(l -> listener.get().onRunClick(ID));
    }
}
