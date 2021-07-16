package com.itba.runningMate.pastruns

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.R
import com.itba.runningMate.di.DependencyContainerLocator.locateComponent
import com.itba.runningMate.domain.Run
import com.itba.runningMate.mainpage.fragments.feed.run.OnRunClickListener
import com.itba.runningMate.pastruns.runs.RunAdapter

class PastRunsActivity : AppCompatActivity(), PastRunsView, OnRunClickListener {

    private lateinit var rvRunListAdapter: RunAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenter: PastRunsPresenter
    private lateinit var emptyMessage: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_mainpage_past_runs)
        val container = locateComponent(this)
        val schedulerProvider = container.getSchedulerProvider()
        val runRepository = container.getRunRepository()
        presenter = PastRunsPresenter(schedulerProvider, runRepository, this)
        emptyMessage = findViewById(R.id.empty_run_list)

        setUpRecyclerView()

        //Creo el botón para volver
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpRecyclerView() {
        recyclerView = findViewById(R.id.old_maps_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        rvRunListAdapter = RunAdapter()
        rvRunListAdapter.setClickListener(this)
        recyclerView.adapter = rvRunListAdapter
    }

    public override fun onStart() {
        super.onStart()
        presenter.onViewAttached()
    }

    public override fun onStop() {
        super.onStop()
        presenter.onViewDetached()
    }

    override fun updatePastRuns(list: List<Run>) {
        rvRunListAdapter.update(list)
    }

    override fun showNoPastRunsMessage() {
        emptyMessage.visibility = View.VISIBLE
    }

    override fun hideNoPastRunsMessage() {
        emptyMessage.visibility = View.GONE
    }

    override fun launchRunDetails(id: Long) {
        val uriBuilder = Uri.Builder()
            .scheme("runningmate")
            .encodedAuthority("run")
            .appendQueryParameter("run-id", id.toString())
        startActivity(Intent(Intent.ACTION_VIEW, uriBuilder.build()))
    }

    override fun onRunClick(id: Long) {
        presenter.onRunClick(id)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}