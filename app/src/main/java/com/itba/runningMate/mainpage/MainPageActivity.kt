package com.itba.runningMate.mainpage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.itba.runningMate.R
import com.itba.runningMate.mainpage.adapters.ViewPagerAdapter
import com.itba.runningMate.mainpage.fragments.feed.FeedFragment
import com.itba.runningMate.mainpage.fragments.running.RunningFragment

class MainPageActivity : AppCompatActivity() {

    private lateinit var titleList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainpage)
        titleList = mutableListOf(getString(R.string.run), getString(R.string.feed))
        setUpTabs()
    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(RunningFragment())
        adapter.addFragment(FeedFragment())
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(
            tabLayout, viewPager
        ) { tab: TabLayout.Tab, position: Int -> tab.text = titleList[position] }.attach()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // ref: https://medium.com/programming-lite/runtime-permissions-in-android-7496a5f3de55
        // todo: create un permissions manager que te maneje todo esto
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}