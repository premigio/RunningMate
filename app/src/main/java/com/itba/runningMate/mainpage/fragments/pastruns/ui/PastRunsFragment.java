package com.itba.runningMate.mainpage.fragments.pastruns.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.itba.runningMate.R;
import com.itba.runningMate.di.DependencyContainer;
import com.itba.runningMate.di.DependencyContainerLocator;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.mainpage.fragments.pastruns.runs.ui.OnRunClickListener;
import com.itba.runningMate.mainpage.fragments.pastruns.runs.ui.RunAdapter;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.util.List;

public class PastRunsFragment extends Fragment implements PastRunsView, OnRunClickListener {

    private RunAdapter rvRunListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PastRunsPresenter presenter;
    private TextView emptyMessage;

    public PastRunsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mainpage_past_runs, container, false);
    }

    private void setUpRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.old_maps_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvRunListAdapter = new RunAdapter();
        rvRunListAdapter.setClickListener(this);
        recyclerView.setAdapter(rvRunListAdapter);
    }

    private void setUpRefreshLayout(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_old_runs);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.refreshAction();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final DependencyContainer container = DependencyContainerLocator.locateComponent(this.getActivity());
        final SchedulerProvider schedulerProvider = container.getSchedulerProvider();
        final RunRepository runRepository = container.getRunRepository();

        presenter = new PastRunsPresenter(schedulerProvider, runRepository, this);

        emptyMessage = view.findViewById(R.id.empty_run_list);
        setUpRecyclerView(view);
        setUpRefreshLayout(view);

    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onViewAttached();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onViewDetached();
    }

    @Override
    public void updatePastRuns(List<Run> list) {
        rvRunListAdapter.update(list);
    }

    @Override
    public void showNoPastRunsMessage() {
        emptyMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoPastRunsMessage() {
        emptyMessage.setVisibility(View.GONE);
    }

    @Override
    public void launchRunDetails(long id) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("runningmate")
                .encodedAuthority("run")
                .appendQueryParameter("run-id", Long.toString(id));
        startActivity(new Intent(Intent.ACTION_VIEW, uriBuilder.build()));
    }

    @Override
    public void onRunClick(long id) {
        presenter.onRunClick(id);
    }
}
