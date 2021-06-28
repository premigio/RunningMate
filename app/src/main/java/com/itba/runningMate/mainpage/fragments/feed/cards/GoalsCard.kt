package com.itba.runningMate.mainpage.fragments.feed.cards

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.itba.runningMate.R
import java.lang.ref.WeakReference

class GoalsCard : CardView {

    private lateinit var image: ImageView
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var seeAll: Button
    private var onSeeAllClickListener: WeakReference<OnSeeAllClickListener?>? = null

    constructor(context: Context) : super(context) {
        prepareFromConstructor(context)
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

    private fun prepareFromConstructor(context: Context) {
        inflate(context, R.layout.card_goals, this)
        image = findViewById(R.id.goal_image_card)
        title = findViewById(R.id.goal_title_card)
        subtitle = findViewById(R.id.goal_subtitle_card)
        seeAll = findViewById(R.id.see_all_goals)
        seeAll.setOnClickListener(OnClickListener { l: View? -> onSeeAllButtonClicked() })
    }

    private fun onSeeAllButtonClicked() {
        if (onSeeAllClickListener!!.get() != null) {
            onSeeAllClickListener!!.get()!!.onSeeAllAchievementsClick()
        }
    }

    fun setTitle(titleInt: Int) {
        title.setText(titleInt)
    }

    fun setSubtitle(subtitleInt: Int) {
        subtitle.setText(subtitleInt)
    }

    fun setImage(imageInt: Int) {
        image.setImageResource(imageInt)
    }

    fun setSeeAllListener(onSeeAllClickListener: OnSeeAllClickListener?) {
        this.onSeeAllClickListener = WeakReference(onSeeAllClickListener)
    }
}