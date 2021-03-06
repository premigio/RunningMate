package com.itba.runningMate.mainpage.fragments.feed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.itba.runningMate.R
import com.itba.runningMate.components.run.OnRunClickListener
import com.itba.runningMate.di.DependencyContainerLocator.locateComponent
import com.itba.runningMate.domain.Achievements
import com.itba.runningMate.domain.Level
import com.itba.runningMate.domain.Run
import com.itba.runningMate.mainpage.fragments.feed.cards.AchievementsCard
import com.itba.runningMate.mainpage.fragments.feed.cards.LevelsCard
import com.itba.runningMate.mainpage.fragments.feed.cards.listeners.OnSeeAllAchievementsListener
import com.itba.runningMate.mainpage.fragments.feed.cards.listeners.OnSeeAllLevelsListener
import com.itba.runningMate.mainpage.fragments.feed.cards.listeners.OnSeeAllPastRunsListener
import com.itba.runningMate.mainpage.fragments.feed.cards.RecentActivityCard

class FeedFragment : Fragment(), FeedView, OnRunClickListener, OnSeeAllPastRunsListener,
    OnSeeAllLevelsListener, OnSeeAllAchievementsListener {

    private lateinit var presenter: FeedPresenter
    private lateinit var recentActivityCard: RecentActivityCard
    private lateinit var levelCard: LevelsCard
    private lateinit var achievementsCard: AchievementsCard

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createPresenter()
        recentActivityCard = view.findViewById(R.id.past_run_card)
        levelCard = view.findViewById(R.id.level_card)
        achievementsCard = view.findViewById(R.id.achievements_card)
        recentActivityCard.setElementListener(this)
        recentActivityCard.setSeeAllListener(this)
        levelCard.setSeeAllListener(this)
        achievementsCard.setSeeAllListener(this)
    }

    private fun createPresenter() {
        val container = locateComponent(requireContext())
        val runRepository = container.getRunRepository()
        val achievementsRepository = container.getAchievementsRepository()
        val schedulerProvider = container.getSchedulerProvider()
        val aggregateRunMetricsStorage = container.getAggregateRunMetricsStorage()
        presenter = FeedPresenter(
            runRepository,
            achievementsRepository,
            schedulerProvider,
            aggregateRunMetricsStorage,
            this
        )
    }

    override fun launchPastRunsActivity() {
        val uriBuilder = Uri.Builder()
            .scheme("runningmate")
            .encodedAuthority("pastruns")
        startActivity(Intent(Intent.ACTION_VIEW, uriBuilder.build()))
    }

    override fun launchRunDetailActivity(runId: Long) {
        val uriBuilder = Uri.Builder()
            .scheme("runningmate")
            .encodedAuthority("run")
            .appendQueryParameter("run-id", runId.toString())
        startActivity(Intent(Intent.ACTION_VIEW, uriBuilder.build()))
    }

    override fun launchAchievementsActivity() {
        val uriBuilder = Uri.Builder()
            .scheme("runningmate")
            .encodedAuthority("achievements")
        startActivity(Intent(Intent.ACTION_VIEW, uriBuilder.build()))
    }

    override fun launchLevelsActivity() {
        val uriBuilder = Uri.Builder()
            .scheme("runningmate")
            .encodedAuthority("levels")
        startActivity(Intent(Intent.ACTION_VIEW, uriBuilder.build()))
    }

    override fun onStart() {
        super.onStart()
        presenter.onViewAttached()
    }

    override fun onStop() {
        super.onStop()
        presenter.onViewDetached()
    }

    override fun showRecentActivity(recentRuns: List<Run>) {
        recentActivityCard.bind(recentRuns)
    }

    override fun showAchievements(achievements: List<Achievements>) {
        achievementsCard.bind(achievements)
    }

    override fun showCurrentLevel(level: Level, distance: Double) {
        levelCard.bind(level, distance)
    }

    override fun onRunClick(id: Long) {
        presenter.onPastRunClick(id)
    }

    override fun onSeeAllPastRunsClick() {
        presenter.goToPastRunsActivity()
    }

    override fun onSeeAllAchievementsClick() {
        presenter.goToAchievementsActivity()
    }

    override fun onSeeAllLevelsClick() {
        presenter.goToLevelsActivity()
    }

    override fun startLevelShimmerAnimation() {
        levelCard.startShimmerAnimation()
    }

    override fun stopLevelShimmerAnimation() {
        levelCard.stopShimmerAnimation()
    }

    override fun startRecentActivityShimmerAnimation() {
        recentActivityCard.startShimmerAnimation()
    }

    override fun stopRecentActivityShimmerAnimation() {
        recentActivityCard.stopShimmerAnimation()
    }

    override fun startAchievementsShimmerAnimation() {
        achievementsCard.startShimmerAnimation()
    }

    override fun stopAchievementsShimmerAnimation() {
        achievementsCard.stopShimmerAnimation()
    }
}