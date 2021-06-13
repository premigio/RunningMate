package com.itba.runningMate.mainpage.fragments.feed.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.itba.runningMate.R;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.pastruns.runs.ui.OnRunClickListener;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.Nullable;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;

public class RunElementView extends FrameLayout implements View.OnClickListener{

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
    private long ID;

    private WeakReference<OnRunClickListener> listener;

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

    public void bind(Run model) {

        if (model == null) return;

        TextView title, distance, time;

        title = this.findViewById(R.id.run_list_card_title);
        distance = this.findViewById(R.id.run_list_distance_content);
        time = this.findViewById(R.id.run_list_time_run);
        ID = model.getUid();
        title.setText(getContext().getString(R.string.past_title,dateFormat.format(model.getStartTime())));
        distance.setText(getContext().getString(R.string.distance_string,model.getDistance()));
        time.setText(timeFormat.format(model.getStartTime()));

    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        listener.get().onRunClick(ID);
    }

    public void setOnClickListener(OnRunClickListener listener){
        this.listener = new WeakReference<>(listener);
    }
}