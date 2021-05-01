package com.itba.runningMate.fragments.pastruns;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itba.runningMate.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewRunListAdapter extends RecyclerView.Adapter<RLViewHolder> {

    private final List<DummyRView> runList;
    private WeakReference<OnRunClickListener> listener;

    public RecyclerViewRunListAdapter() {
        runList = new ArrayList<>();
    }

    public void update(List<DummyRView> runList) {
        this.runList.clear();
        if (runList != null) {
            this.runList.addAll(runList);
        }
        notifyDataSetChanged();
    }

    public void setClickListener(OnRunClickListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.old_map_rv_row;
    }

    @NonNull
    @Override
    public RLViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RLViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RLViewHolder holder, int position) {
        holder.bind(runList.get(position));
        holder.setOnClickListener(listener.get());
    }


    @Override
    public int getItemCount() {
        return runList.size();
    }

}