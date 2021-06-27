package com.itba.runningMate.mainpage.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class ViewPagerAdapter(fa: FragmentActivity?) : FragmentStateAdapter(fa!!) {

    private val fragList: MutableList<Fragment>

    override fun createFragment(position: Int): Fragment {
        return fragList[position]
    }

    override fun getItemCount(): Int {
        return fragList.size
    }

    fun addFragment(fragment: Fragment) {
        fragList.add(fragment)
    }

    init {
        fragList = ArrayList()
    }
}