package com.itba.runningMate.pastruns

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.R
import com.itba.runningMate.components.run.OnRunClickListener
import com.itba.runningMate.di.DependencyContainerLocator.locateComponent
import com.itba.runningMate.domain.Run
import com.itba.runningMate.pastruns.runs.OnRunDeleteListener
import com.itba.runningMate.pastruns.runs.RunAdapter
import com.itba.runningMate.pastruns.runs.SwipeToDeleteCallback

class PastRunsActivity : AppCompatActivity(), PastRunsView, OnRunClickListener,
    OnRunDeleteListener {

    private lateinit var rvRunListAdapter: RunAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenter: PastRunsPresenter
    private lateinit var emptyMessage: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_runs)

        createPresenter()

        emptyMessage = findViewById(R.id.empty_run_list)

        setUpRecyclerView()

        //Creo el botón para volver
        val actionBar = supportActionBar
        actionBar!!.setTitle(R.string.past_runs)
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    private fun createPresenter() {
        val container = locateComponent(this)
        val schedulerProvider = container.getSchedulerProvider()
        val runRepository = container.getRunRepository()
        val achievementsStorage = container.getAchievementsStorage()
        presenter = PastRunsPresenter(schedulerProvider, runRepository, achievementsStorage, this)
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
        rvRunListAdapter.setSwipeRunToDeleteListener(this)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(this, rvRunListAdapter))
        recyclerView.adapter = rvRunListAdapter
        itemTouchHelper.attachToRecyclerView(recyclerView)
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

    override fun onSwipeRunToDelete(id: Long) {
        presenter.onSwipeRunToDelete(id)
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

    override fun showDeleteError() {
        presenter.fetchRuns() // refresh action
        Toast.makeText(this, "Error while attempting to delete run", Toast.LENGTH_LONG).show()
    }
}