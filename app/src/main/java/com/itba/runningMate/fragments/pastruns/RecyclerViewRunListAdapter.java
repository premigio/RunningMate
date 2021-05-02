package com.itba.runningMate.fragments.pastruns;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itba.runningMate.R;
import com.itba.runningMate.domain.Sprint;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;

public class RecyclerViewRunListAdapter extends RecyclerView.Adapter<RLViewHolder> {

    private final List<Sprint> currentRunList;
    private WeakReference<OnRunClickListener> listener;

    public RecyclerViewRunListAdapter() {
        currentRunList = new ArrayList<>();
    }

    public void update(List<Sprint> runList) {
        this.currentRunList.clear();
        if (runList != null) {
            this.currentRunList.addAll(runList);
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
        if (listener == null) {
            return;
        }
        holder.bind(currentRunList.get(position));
        holder.setOnClickListener(listener.get());
    }

    @Override
    public long getItemId(int position) {
        if (currentRunList == null) {
            return NO_ID;
        }

        Sprint pos = currentRunList.get(position);

        return pos == null? NO_ID : pos.getUid();
    }

    @Override
    public int getItemCount() {
        return currentRunList.size();
    }

}