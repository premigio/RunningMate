package com.itba.runningMate.levels

import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.R
import com.itba.runningMate.di.DependencyContainerLocator
import com.itba.runningMate.domain.Level
import com.itba.runningMate.levels.level.LevelAdapter

class LevelsActivity : AppCompatActivity(), LevelsView {

    private lateinit var adapter: LevelAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenter: LevelsPresenter
    private lateinit var image: ImageView
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressKm: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_levels)
        supportActionBar!!.setTitle(R.string.level)

        setUp()

        createPresenter()

        setUpRecyclerView()
    }

    private fun setUp() {
        // view elements
        image = findViewById(R.id.level_image)
        title = findViewById(R.id.level_title)
        description = findViewById(R.id.level_subtitle)
        progressBar = findViewById(R.id.current_level_progress_bar)
        progressKm = findViewById(R.id.current_level_progress_km)

        // back btn
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun createPresenter() {
        val container = DependencyContainerLocator.locateComponent(this)
        val aggregateRunMetricsStorage = container.getAggregateRunMetricsStorage()
        presenter = LevelsPresenter(aggregateRunMetricsStorage, this)
    }

    override fun onStart() {
        super.onStart()

        presenter.onViewAttached()
    }

    override fun onStop() {
        super.onStop()

        presenter.onViewDetached()
    }

    override fun showCurrentLevel(level: Level, distance: Double) {
        title.setText(level.title)
        description.setText(level.subTitle)
        image.setImageResource(level.image)
        progressBar.max = level.sizeKm.toInt()
        progressBar.progress = (distance - level.minKm).toInt()
        val text: String =
            getString(R.string.current_level_progress_km_html, distance, level.minKm + level.sizeKm)
        val styledText: Spanned = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        progressKm.text = styledText
        adapter.update(level)
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

    private fun setUpRecyclerView() {
        recyclerView = findViewById(R.id.levels_rv)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LevelAdapter()
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
    }

}