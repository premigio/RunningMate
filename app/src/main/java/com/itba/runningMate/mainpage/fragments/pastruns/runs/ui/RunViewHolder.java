package com.itba.runningMate.mainpage.fragments.pastruns.runs.ui;

import android.view.View;
import android.widget.TextView;

import com.itba.runningMate.R;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.mainpage.fragments.pastruns.runs.ui.OnRunClickListener;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;

class RunViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
    private long id = NO_ID;
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
    private WeakReference<OnRunClickListener> listener;


    public RunViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    public void bind(Run model) {

        if (model == null) return;

        TextView title, distance, time;
        id = model.getUid();

        title = itemView.findViewById(R.id.run_list_card_title);
        distance = itemView.findViewById(R.id.run_list_distance_content);
        time = itemView.findViewById(R.id.run_list_time_run);

        title.setText(itemView.getContext().getString(R.string.past_title,dateFormat.format(model.getStartTime())));
        distance.setText(itemView.getContext().getString(R.string.distance_string,model.getDistance()));
        time.setText(timeFormat.format(model.getStartTime()));


    }

    @Override
    public void onClick(View v) {
        //todo: hacer que se lea el getItemId() del adapter
        if (listener == null) {
            return;
        }
        listener.get().onRunClick(id);
    }

    public void setOnClickListener(OnRunClickListener listener){
        this.listener = new WeakReference<>(listener);
    }

}
