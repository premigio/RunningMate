package com.itba.runningMate.achievements

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.itba.runningMate.R
import com.itba.runningMate.achievements.achievement.Achievements
import com.itba.runningMate.achievements.achievement.AchievementsElementView
import com.itba.runningMate.di.DependencyContainerLocator.locateComponent
import java.util.*

class AchievementsActivity : AppCompatActivity(), AchievementsView {

    private lateinit var presenter: AchievementsPresenter
    private lateinit var achievements: MutableList<AchievementsElementView>

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)
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
        achievements = ArrayList()
        var achievement: AchievementsElementView = findViewById(R.id.achievement1)
        achievement.bind(
            getString(R.string.total_distance_achievement_title),
            getString(R.string.total_distance_achievement_subtitle)
        )
        achievements.add(achievement)
        achievement = findViewById(R.id.achievement2)
        achievement.bind(
            getString(R.string.max_kcal_achievement_title),
            getString(R.string.max_kcal_achievement_subtitle)
        )
        achievements.add(achievement)
        achievement = findViewById(R.id.achievement3)
        achievement.bind(
            getString(R.string.max_speed_achievement_title),
            getString(R.string.max_speed_achievement_subtitle)
        )
        achievements.add(achievement)
        achievement = findViewById(R.id.achievement4)
        achievement.bind(
            getString(R.string.max_time_achievement_title),
            getString(R.string.max_time_achievement_subtitle)
        )
        achievements.add(achievement)
    }

    override fun onStart() {
        super.onStart()
        presenter.onViewAttached()
    }

    override fun onStop() {
        super.onStop()
        presenter.onViewDetached()
    }

    override fun setAchievement(achievementNumber: Achievements, achieved: Boolean) {
        achievements[achievementNumber.ordinal].setBadgeVisibility(achieved)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}