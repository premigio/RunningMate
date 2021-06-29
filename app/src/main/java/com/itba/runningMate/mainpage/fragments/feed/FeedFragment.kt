package com.itba.runningMate.mainpage.fragments.feed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.itba.runningMate.R
import com.itba.runningMate.di.DependencyContainerLocator.locateComponent
import com.itba.runningMate.domain.Run
import com.itba.runningMate.mainpage.fragments.feed.cards.GoalsCard
import com.itba.runningMate.mainpage.fragments.feed.cards.OnSeeAllClickListener
import com.itba.runningMate.mainpage.fragments.feed.cards.PastRunsCard
import com.itba.runningMate.mainpage.fragments.feed.run.OnRunClickListener

class FeedFragment : Fragment(), FeedView, OnRunClickListener, OnSeeAllClickListener {

    private lateinit var presenter: FeedPresenter
    private lateinit var pastRunsCard: PastRunsCard
    private lateinit var goalsCard: GoalsCard

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
        goalsCard = view.findViewById(R.id.goals_card)
        pastRunsCard.setElementListener(this)
        pastRunsCard.setSeeAllListener(this)
        goalsCard.setSeeAllListener(this)
    }

    private fun createPresenter() {
        val container = locateComponent(context!!)
        presenter = FeedPresenter(
            container.getRunRepository(),
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
            .appendQueryParameter("run-id", java.lang.Long.toString(runId))
        startActivity(Intent(Intent.ACTION_VIEW, uriBuilder.build()))
    }

    override fun launchAchievementsActivity() {
        val uriBuilder = Uri.Builder()
            .scheme("runningmate")
            .encodedAuthority("achievements")
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

    override fun disappearRuns(i: Int) {
        pastRunsCard.disappearRuns(i)
    }

    override fun disappearNoText() {
        pastRunsCard.disappearNoText()
    }

    override fun setGoalTitle(title: Int) {
        goalsCard.setTitle(title)
    }

    override fun setGoalSubtitle(subtitle: Int) {
        goalsCard.setSubtitle(subtitle)
    }

    override fun setGoalImage(image: Int) {
        goalsCard.setImage(image)
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
}