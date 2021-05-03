package com.itba.runningMate.mainpage.fragments.pastruns.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itba.runningMate.R;
import com.itba.runningMate.db.SprintDb;
import com.itba.runningMate.domain.Sprint;
import com.itba.runningMate.mainpage.fragments.pastruns.OnRunClickListener;
import com.itba.runningMate.mainpage.fragments.pastruns.RecyclerViewRunListAdapter;
import com.itba.runningMate.repository.sprint.SprintRepositoryImpl;
import com.itba.runningMate.utils.schedulers.AndroidSchedulerProvider;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class RunListFragment extends Fragment implements RunListView, OnRunClickListener {

    private static final String SPRINT_DETAILS_URL = "app://sprint_detail";

    private RunListPresenter presenter;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerViewRunListAdapter rvRunListAdapter;

    public RunListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past_runs, container, false);
    }

    private void setUpRecyclerView(View view) {

        recyclerView = view.findViewById(R.id.old_maps_recycler_view);

        recyclerView.setHasFixedSize(true);

        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL));

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        rvRunListAdapter = new RecyclerViewRunListAdapter();
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

        SchedulerProvider sp = new AndroidSchedulerProvider();

        presenter = new RunListPresenter(sp,
                new SprintRepositoryImpl(SprintDb.getInstance(
                        this.getActivity().getApplicationContext()).SprintDao(),
                        sp),
                this);

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
    public void updateOldRuns(List<Sprint> list) {
        rvRunListAdapter.update(list);
    }

    @Override
    public void showModelToast(long modelId) {
        Toast.makeText(this.getContext(),
                "modelIdClicked = " + modelId,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void callSprintDetails(long id) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("app")
                .encodedAuthority("sprint_detail")
                .appendQueryParameter("sprintid",Long.toString(id));
        startActivity(new Intent(Intent.ACTION_VIEW, uriBuilder.build()));
    }

    @Override
    public void onRunClick(long id) {
        presenter.onRunClick(id);
    }
}
