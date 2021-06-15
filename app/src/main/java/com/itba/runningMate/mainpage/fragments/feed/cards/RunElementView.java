package com.itba.runningMate.mainpage.fragments.feed.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.itba.runningMate.R;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.mainpage.fragments.feed.FeedPresenter;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class RunElementView extends FrameLayout {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
    private long ID;

    private WeakReference<OnCardClickListener> listener;

    public RunElementView(Context context) {
        super(context);
        inflate(context, R.layout.run_element, this);
    }

    public RunElementView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.run_element, this);
    }

    public RunElementView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.run_element, this);
    }

    public void setOnClick(OnCardClickListener listener) {
        this.listener = new WeakReference<OnCardClickListener>(listener);
    }

    public void bind(Run model) {

        if (model == null) return;

        TextView title, distance, time;

        title = this.findViewById(R.id.run_list_card_title);
        distance = this.findViewById(R.id.run_list_distance_content);
        time = this.findViewById(R.id.run_list_time_run);
        ID = model.getUid();
        title.setText( model.getTitle());
        distance.setText(getContext().getString(R.string.distance_string,model.getDistance()));
        time.setText(timeFormat.format(model.getStartTime()));

        ConstraintLayout cl = this.findViewById(R.id.old_run_row_ll);
        if (listener.get() != null)
            cl.setOnClickListener((v) -> listener.get().onRunClick(ID));
    }
}
