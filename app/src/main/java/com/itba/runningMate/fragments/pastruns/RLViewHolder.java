package com.itba.runningMate.fragments.pastruns;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itba.runningMate.R;
import com.itba.runningMate.fragments.history.model.DummyRView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class RLViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private OnRunClickListener listener;


    public RLViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(DummyRView model){

        TextView title, content;

        title = itemView.findViewById(R.id.old_run_title);
        content = itemView.findViewById(R.id.old_run_content);

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

//    @Override
//    public void onClick(View v) {
//        Toast.makeText(v.getContext(),
//                "Title = "
//                        + runList.get(getBindingAdapterPosition()).getTitle()
//                        + "\n Content = "
//                        + runList.get(getBindingAdapterPosition()).getContent(),
//                Toast.LENGTH_SHORT).show();
//    }
}
