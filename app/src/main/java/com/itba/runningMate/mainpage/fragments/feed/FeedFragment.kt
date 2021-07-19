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
import com.itba.runningMate.mainpage.fragments.feed.cards.PastRunsCard
import com.itba.runningMate.mainpage.fragments.feed.cards.listeners.OnSeeAllAchievementsListener
import com.itba.runningMate.mainpage.fragments.feed.cards.listeners.OnSeeAllLevelsListener
import com.itba.runningMate.mainpage.fragments.feed.cards.listeners.OnSeeAllPastRunsListener

class FeedFragment : Fragment(), FeedView, OnRunClickListener, OnSeeAllPastRunsListener,
    OnSeeAllLevelsListener, OnSeeAllAchievementsListener {

    private lateinit var presenter: FeedPresenter
    private lateinit var pastRunsCard: PastRunsCard
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
        pastRunsCard = view.findViewById(R.id.past_run_card)
        levelCard = view.findViewById(R.id.level_card)
        achievementsCard = view.findViewById(R.id.achievements_card)
        pastRunsCard.setElementListener(this)
        pastRunsCard.setSeeAllListener(this)
        levelCard.setSeeAllListener(this)
        achievementsCard.setSeeAllListener(this)
    }

    private fun createPresenter() {
        val container = locateComponent(requireContext())
        presenter = FeedPresenter(
            container.getRunRepository(),
            container.getAchievementsRepository(),
            container.getSchedulerProvider(),
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

    override fun setPastRunCardsNoText() {
        pastRunsCard.setPastRunCardsNoText()
    }

    override fun addRunToCard(i: Int, run: Run) {
        pastRunsCard.addRunToCard(i, run)
    }

    override fun disappearRuns(abs: Int) {
        pastRunsCard.disappearRuns(abs)
    }

    override fun disappearNoText() {
        pastRunsCard.disappearNoText()
    }

    override fun showAchievements(achievements: List<Achievements>) {
        achievementsCard.bind(achievements)
    }

    override fun showCurrentLevel(level: Level) {
        levelCard.bind(level)
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
        pastRunsCard.startShimmerAnimation()
    }

    override fun stopRecentActivityShimmerAnimation() {
        pastRunsCard.stopShimmerAnimation()
    }
}