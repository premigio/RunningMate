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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.itba.runningMate.R
import com.itba.runningMate.di.DependencyContainerLocator.locateComponent
import com.itba.runningMate.domain.Run
import com.itba.runningMate.pastruns.runs.OnRunClickListener
import com.itba.runningMate.pastruns.runs.RunAdapter
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class PastRunsActivity : AppCompatActivity(), PastRunsView, OnRunClickListener {

    private lateinit var rvRunListAdapter: RunAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenter: PastRunsPresenter
    private lateinit var emptyMessage: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_mainpage_past_runs)
        val container = locateComponent(this)
        val scope = CoroutineScope(Dispatchers.IO + CoroutineName("PastRunsScope"))
        val runRepository = container.getRunRepository()
        presenter = PastRunsPresenter(scope, runRepository, this)
        emptyMessage = findViewById(R.id.empty_run_list)
        setUpRecyclerView()
        setUpRefreshLayout()

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
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        rvRunListAdapter = RunAdapter()
        rvRunListAdapter.setClickListener(this)
        recyclerView.setAdapter(rvRunListAdapter)
    }

    private fun setUpRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_old_runs)
        swipeRefreshLayout.setOnRefreshListener {
            presenter.refreshAction()
            swipeRefreshLayout.setRefreshing(false)
        }
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
            .appendQueryParameter("run-id", java.lang.Long.toString(id))
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