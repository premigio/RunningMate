package com.itba.runningMate.running

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.itba.runningMate.R
import com.itba.runningMate.running.adapters.ScreenSlidePagerAdapter
import com.itba.runningMate.running.fragments.map.RunningMapFragment
import com.itba.runningMate.running.fragments.metrics.RunningMetricsFragment
import com.itba.runningMate.running.fragments.music.RunningMusicFragment
import com.itba.runningMate.running.transformers.ZoomOutPageTransformer

class RunningActivity : AppCompatActivity(), RunningView {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: TabLayout
    private lateinit var presenter: RunningPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar!!.hide()
        createPresenter()
        setContentView(R.layout.activity_running)
        viewPager = findViewById(R.id.pager)
        dotsIndicator = findViewById(R.id.tabLayout)
        setUpTabs()
    }

    private fun createPresenter() {
        presenter = RunningPresenter()
    }

    override fun onStart() {
        super.onStart()
        presenter.onViewAttached()
    }

    public override fun onStop() {
        super.onStop()
        presenter.onViewDetached()
    }

    private fun setUpTabs() {
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        pagerAdapter.addFragment(RunningMusicFragment())
        pagerAdapter.addFragment(RunningMetricsFragment())
        pagerAdapter.addFragment(RunningMapFragment())
        viewPager.adapter = pagerAdapter
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        viewPager.setCurrentItem(1,false)
        TabLayoutMediator(
            dotsIndicator, viewPager
        ) { _: TabLayout.Tab?, _: Int -> }.attach()
    }

    override fun onBackPressed() {
        /* Activity ends when stop button is pressed */
    }
}