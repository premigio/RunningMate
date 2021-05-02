package com.itba.runningMate.fragments.pastruns;

import android.view.View;
import android.widget.TextView;

import com.itba.runningMate.R;
import com.itba.runningMate.domain.Sprint;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;

class RLViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private OnRunClickListener listener;
    private long id = NO_ID;


    public RLViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    public void bind(Sprint model){

        if (model == null) return;

        TextView title, distance, time;
        id = model.getUid();

        title = itemView.findViewById(R.id.run_list_card_title);
        distance = itemView.findViewById(R.id.run_list_distance_content);
        time = itemView.findViewById(R.id.run_list_time_run);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());


        title.setText(itemView.getContext().getString(R.string.past_title,dateFormat.format(model.getStartTime())));
        distance.setText(itemView.getContext().getString(R.string.distance_string,model.getDistance()));
        time.setText(timeFormat.format(model.getStartTime()));


    }

    @Override
    public void onClick(View v) {
        //todo: hacer que se lea el getItemId() del adapter
        listener.onRunClick(id);
    }

    public void setOnClickListener(OnRunClickListener listener){
        this.listener = listener;
    }

}
