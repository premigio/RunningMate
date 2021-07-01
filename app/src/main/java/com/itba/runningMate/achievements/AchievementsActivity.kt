package com.itba.runningMate.achievements

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.itba.runningMate.R
import com.itba.runningMate.achievements.achievement.Achievements
import com.itba.runningMate.achievements.achievement.AchievementsElementView
import com.itba.runningMate.di.DependencyContainerLocator.locateComponent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*

class AchievementsActivity : AppCompatActivity(), AchievementsView {

    private lateinit var presenter: AchievementsPresenter
    private lateinit var goalTitle: TextView
    private lateinit var goalSubtitle: TextView
    private lateinit var goalImage: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var achievements: MutableList<AchievementsElementView>

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)
        createPresenter()
        setUp()
    }

    private fun createPresenter() {
        val container = locateComponent(this)
        val runRepository = container.getRunRepository()
        val stateStorage = container.getAchievementsStorage()
        val scope = CoroutineScope(Dispatchers.IO + CoroutineName("AchievementsScope"))
        presenter = AchievementsPresenter(runRepository, stateStorage, scope, this)
    }

    private fun setUp() {
        goalTitle = findViewById(R.id.goal_title)
        goalSubtitle = findViewById(R.id.goal_subtitle)
        goalImage = findViewById(R.id.goal_image)
        progressBar = findViewById(R.id.goal_progress_bar)
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

    override fun setGoalTitle(title: Int) {
        goalTitle.setText(title)
    }

    override fun setGoalSubtitle(subtitle: Int) {
        goalSubtitle.setText(subtitle)
    }

    override fun setGoalImage(image: Int) {
        goalImage.setImageResource(image)
    }

    override fun setProgressBar(distance: Double, max: Double) {
        progressBar.max = max.toInt()
        progressBar.progress = distance.toInt()
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