package com.itba.runningMate.pastruns.runs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itba.runningMate.R;
import com.itba.runningMate.domain.Run;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;

public class RunAdapter extends RecyclerView.Adapter<RunViewHolder> {

    private final List<Run> currentRunList;
    private WeakReference<OnRunClickListener> listener;

    public RunAdapter() {
        currentRunList = new ArrayList<>();
    }

    public void update(List<Run> runList) {
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
        return R.layout.run_element;
    }

    @NonNull
    @Override
    public RunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new RunViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RunViewHolder holder, int position) {
        if (listener == null) {
            return;
        }
        holder.bind(currentRunList.get(position));
        holder.setOnClickListener(listener.get());
    }

    @Override
    public long getItemId(int position) {
        if (currentRunList.isEmpty()) {
            return NO_ID;
        }

        Run pos = currentRunList.get(position);

        return pos == null ? NO_ID : pos.getUid();
    }

    @Override
    public int getItemCount() {
        return currentRunList.size();
    }

}