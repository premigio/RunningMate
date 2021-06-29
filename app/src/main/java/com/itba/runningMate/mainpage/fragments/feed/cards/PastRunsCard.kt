package com.itba.runningMate.mainpage.fragments.feed.cards

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.itba.runningMate.R
import com.itba.runningMate.domain.Run
import com.itba.runningMate.mainpage.fragments.feed.run.OnRunClickListener
import com.itba.runningMate.mainpage.fragments.feed.run.RunElementView
import java.lang.ref.WeakReference
import java.util.*

class PastRunsCard : CardView {

    private lateinit var pastRunsEmptyMessage: TextView
    private lateinit var runs: MutableList<RunElementView>
    private lateinit var seeAll: Button
    private lateinit var runElementListener: WeakReference<OnRunClickListener>
    private lateinit var onSeeAllClickListener: WeakReference<OnSeeAllClickListener>

    constructor(context: Context) : super(context) {
        prepareFromConstructor(context)
    }

    private fun prepareFromConstructor(context: Context) {
        inflate(context, R.layout.card_past_runs, this)
        runs = ArrayList()
        runs.add(findViewById(R.id.past_run_card_1))
        runs.add(findViewById(R.id.past_run_card_2))
        runs.add(findViewById(R.id.past_run_card_3))
        pastRunsEmptyMessage = findViewById(R.id.past_run_empty_card)
        seeAll = findViewById(R.id.see_all_past_runs)
        seeAll.setOnClickListener { l: View? -> onSeeAllButtonClicked() }
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        prepareFromConstructor(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        prepareFromConstructor(context)
    }

    fun setPastRunCardsNoText() {
        pastRunsEmptyMessage.visibility = VISIBLE
    }

    fun disappearNoText() {
        pastRunsEmptyMessage.visibility = GONE
    }

    fun addRunToCard(i: Int, run: Run?) {
        runs[i].visibility = VISIBLE
        if (runElementListener.get() != null) {
            runs[i].setOnClick(runElementListener.get())
        }
        runs[i].bind(run)
    }

    fun disappearRuns(i: Int) {
        var j = i
        if (j >= 3) return
        while (j < 3) {
            runs[j++].visibility = GONE
        }
    }

    private fun onSeeAllButtonClicked() {
        if (onSeeAllClickListener.get() != null) {
            onSeeAllClickListener.get()!!.onSeeAllPastRunsClick()
        }
    }

    fun setElementListener(feedFragment: OnRunClickListener?) {
        runElementListener = WeakReference(feedFragment)
    }

    fun setSeeAllListener(onSeeAllClickListener: OnSeeAllClickListener?) {
        this.onSeeAllClickListener = WeakReference(onSeeAllClickListener)
    }
}