package com.itba.runningMate.fragments.pastruns;

import android.view.View;
import android.widget.TextView;

import com.itba.runningMate.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class RLViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private OnRunClickListener listener;


    public RLViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    public void bind(DummyRView model){

        if (model == null) return;

        TextView title, content;

        title = itemView.findViewById(R.id.run_list_card_title);
        content = itemView.findViewById(R.id.run_list_distance_content);

        title.setText(model.getTitle());
        content.setText(model.getTitle());

    }

    @Override
    public void onClick(View v) {
        listener.onRunClick(getBindingAdapterPosition());
    }

    public void setOnClickListener(OnRunClickListener listener){
        this.listener = listener;
    }

}
