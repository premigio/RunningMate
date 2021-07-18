package com.itba.runningMate.achievements

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itba.runningMate.R
import com.itba.runningMate.achievements.achievement.AchievementAdapter
import com.itba.runningMate.achievements.achievement.AchievementElementView
import com.itba.runningMate.di.DependencyContainerLocator.locateComponent
import com.itba.runningMate.domain.Achievements

class AchievementsActivity : AppCompatActivity(), AchievementsView {

    private lateinit var adapter: AchievementAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var presenter: AchievementsPresenter
    private lateinit var achievements: MutableList<AchievementElementView>

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)
        supportActionBar!!.setTitle(R.string.achievements)

        createPresenter()
        setUp()
    }

    private fun createPresenter() {
        val container = locateComponent(this)
        val schedulerProvider = container.getSchedulerProvider()
        val runRepository = container.getRunRepository()
        val stateStorage = container.getAchievementsStorage()
        presenter = AchievementsPresenter(runRepository, schedulerProvider, stateStorage, this)
    }

    private fun setUp() {
        recyclerView = findViewById(R.id.achievements_rv)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AchievementAdapter()
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
    }

    override fun onStart() {
        super.onStart()

        presenter.onViewAttached()
    }

    override fun onStop() {
        super.onStop()

        presenter.onViewDetached()
    }

    override fun showAchievements(achievements: Array<Achievements>) {
        adapter.update(achievements)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}