package com.itba.runningMate.mainpage.fragments.feed.cards

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.facebook.shimmer.ShimmerFrameLayout
import com.itba.runningMate.R
import com.itba.runningMate.components.run.OnRunClickListener
import com.itba.runningMate.components.run.RunElementView
import com.itba.runningMate.domain.Run
import com.itba.runningMate.mainpage.fragments.feed.cards.listeners.OnSeeAllPastRunsListener
import java.lang.ref.WeakReference

class RecentActivityCard : CardView {

    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var recentActivityEmptyMessage: TextView
    private lateinit var seeAll: Button
    private lateinit var runElements: List<RunElementView>
    private lateinit var runElementListener: WeakReference<OnRunClickListener>
    private lateinit var onSeeAllClickListener: WeakReference<OnSeeAllPastRunsListener>

    constructor(context: Context) : super(context) {
        setUp(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setUp(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setUp(context)
    }

    private fun setUp(context: Context) {
        inflate(context, R.layout.card_recent_activity, this)
        shimmer = findViewById(R.id.recent_activity_shimmer_view)
        runElements = listOf(
            findViewById(R.id.past_run_card_1),
            findViewById(R.id.past_run_card_2),
            findViewById(R.id.past_run_card_3)
        )
        recentActivityEmptyMessage = findViewById(R.id.past_run_empty_card)
        seeAll = findViewById(R.id.see_all_past_runs)
        seeAll.setOnClickListener { onSeeAllButtonClicked() }
    }

    fun bind(recentActivity: List<Run>) {
        if (recentActivity.isEmpty()) {
            recentActivityEmptyMessage.visibility = VISIBLE
            hideRuntElements()
        } else {
            recentActivityEmptyMessage.visibility = GONE
            showRunElements(recentActivity)
        }
    }

    private fun hideRuntElements() {
        for (i in runElements.indices) {
            runElements[i].visibility = GONE
        }
    }

    private fun showRunElements(recentActivity: List<Run>) {
        for (i in runElements.indices) {
            if (i < recentActivity.size) {
                runElements[i].setOnClick(runElementListener.get())
                runElements[i].bind(recentActivity[i])
                runElements[i].visibility = VISIBLE
            } else {
                runElements[i].visibility = GONE
            }
        }
    }

    private fun onSeeAllButtonClicked() {
        if (onSeeAllClickListener.get() != null) {
            onSeeAllClickListener.get()!!.onSeeAllPastRunsClick()
        }
    }

    fun setElementListener(onRunClickListener: OnRunClickListener) {
        runElementListener = WeakReference(onRunClickListener)
    }

    fun setSeeAllListener(onSeeAllPastRunsListener: OnSeeAllPastRunsListener?) {
        this.onSeeAllClickListener = WeakReference(onSeeAllPastRunsListener)
    }

    fun startShimmerAnimation() {
        shimmer.startShimmerAnimation()
    }

    fun stopShimmerAnimation() {
        shimmer.stopShimmerAnimation()
        shimmer.visibility = GONE
    }
}