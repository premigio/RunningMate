package com.itba.runningMate.Fragments.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itba.runningMate.Fragments.Interfaces.ClickListener;
import com.itba.runningMate.Model.DummyRView;
import com.itba.runningMate.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewOldRunAdapter
        extends RecyclerView.Adapter<RecyclerViewOldRunAdapter.ORViewHolder> {

    private List<DummyRView> dummyList;
    private ClickListener clickListener;

    public RecyclerViewOldRunAdapter(List<DummyRView> mDummyList) {
        this.dummyList = mDummyList;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.old_map_rv_row;
    }

    @NonNull
    @Override
    public ORViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ORViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ORViewHolder holder, int position) {
        final DummyRView dummy = dummyList.get(position);
        holder.title.setText(dummy.getTitle());
        holder.content.setText(String.valueOf(dummy.getContent()));
    }


    @Override
    public int getItemCount() {
        return dummyList.size();
    }

    public void setOldRunClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ORViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title, content;
        private LinearLayout dummyLayout;


        public ORViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.old_run_title);
            content = itemView.findViewById(R.id.old_run_content);
            dummyLayout = itemView.findViewById(R.id.old_run_row_ll);
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public TextView getContent() {
            return content;
        }

        public void setContent(TextView content) {
            this.content = content;
        }

        public LinearLayout getDummyLayout() {
            return dummyLayout;
        }

        public void setDummyLayout(LinearLayout dummyLayout) {
            this.dummyLayout = dummyLayout;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(),
                    "Title = "
                    + dummyList.get(getBindingAdapterPosition()).getTitle()
                    + "\n Content = "
                    + dummyList.get(getBindingAdapterPosition()).getContent(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}