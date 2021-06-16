package com.itba.runningMate.pastruns;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.itba.runningMate.R;
import com.itba.runningMate.di.DependencyContainer;
import com.itba.runningMate.di.DependencyContainerLocator;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.pastruns.runs.OnRunClickListener;
import com.itba.runningMate.pastruns.runs.RunAdapter;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

import java.util.List;

public class PastRunsActivity extends AppCompatActivity implements PastRunsView, OnRunClickListener {

    private RunAdapter rvRunListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PastRunsPresenter presenter;
    private TextView emptyMessage;

    public PastRunsActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mainpage_past_runs);


        final DependencyContainer container = DependencyContainerLocator.locateComponent(this);
        final SchedulerProvider schedulerProvider = container.getSchedulerProvider();
        final RunRepository runRepository = container.getRunRepository();

        presenter = new PastRunsPresenter(schedulerProvider, runRepository, this);

        emptyMessage = findViewById(R.id.empty_run_list);
        setUpRecyclerView();
        setUpRefreshLayout();

        //Creo el botón para volver
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.old_maps_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rvRunListAdapter = new RunAdapter();
        rvRunListAdapter.setClickListener(this);
        recyclerView.setAdapter(rvRunListAdapter);
    }

    private void setUpRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_old_runs);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.refreshAction();
            swipeRefreshLayout.setRefreshing(false);
        });
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

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
