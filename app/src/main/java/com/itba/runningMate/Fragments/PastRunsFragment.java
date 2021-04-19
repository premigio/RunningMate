package com.itba.runningMate.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itba.runningMate.Fragments.Adapters.RecyclerViewOldRunAdapter;
import com.itba.runningMate.Fragments.Interfaces.ClickListener;
import com.itba.runningMate.Model.DummyRView;
import com.itba.runningMate.R;

import java.util.ArrayList;
import java.util.List;

public class PastRunsFragment extends Fragment {

    List<DummyRView> dummyList;
    RecyclerView recyclerView;
    RecyclerViewOldRunAdapter recyclerViewOldRunAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    public PastRunsFragment() {
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

        recyclerView = view.findViewById(R.id.old_maps_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        dummyList = new ArrayList<>();
        prepareItems();
        recyclerViewOldRunAdapter = new RecyclerViewOldRunAdapter(dummyList);
        recyclerView.setAdapter(recyclerViewOldRunAdapter);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_old_runs);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            dummyList.add(new DummyRView("Item" + dummyList.size(), "dummy with number " + dummyList.size()));
            recyclerViewOldRunAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    private void prepareItems() { //todo: aunque esto se va al jocara, cambiarlo al presenter
        for (int i = 0; i < 15; i++) {
            DummyRView dummy = new DummyRView("Item" + i, "dummy with number " + i);
            dummyList.add(dummy);
        }
    }

}