package com.itba.runningMate.fragments.pastruns;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itba.runningMate.R;
import com.itba.runningMate.fragments.history.model.DummyRView;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;

public class RunListFragment extends Fragment implements RunListView {

    private RunListPresenter presenter;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerViewRunListAdapter rvRunListAdapter;

    WeakReference<OnRunClickListener> listener;

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
        View view = inflater.inflate(R.layout.fragment_past_runs, container, false);

        setUpRecyclerView(view);
        setUpRefreshLayout(view);

        return view;
    }

    private void setUpRecyclerView(View view){

        recyclerView = view.findViewById(R.id.old_maps_recycler_view);

        recyclerView.setHasFixedSize(true);

        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(),
                DividerItemDecoration.VERTICAL));

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        rvRunListAdapter = new RecyclerViewRunListAdapter();
        recyclerView.setAdapter(rvRunListAdapter);
    }

    private void setUpRefreshLayout(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_old_runs);
        swipeRefreshLayout.setOnRefreshListener(() ->{
            presenter.refreshAction();
            rvRunListAdapter.update(presenter.model);
            rvRunListAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new RunListPresenter(this);
        listener = new WeakReference<>(position -> presenter.onRunClick(position));
        rvRunListAdapter.setClickListener(listener);

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
    public void updateOldRuns(List<DummyRView> list){
        rvRunListAdapter.update(list);
    }

    @Override
    public void showModelToast(DummyRView model){
        Toast.makeText(this.getContext(),
                        "Title = "
                                + model.getTitle()
                                + "\n Content = "
                                + model.getContent(),
                        Toast.LENGTH_SHORT).show();
    }
}
