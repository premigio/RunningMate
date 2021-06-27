package com.itba.runningMate.mainpage.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private List<Fragment> fragList;

    public ViewPagerAdapter(FragmentActivity fa) {
        super(fa);
        fragList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragList.size();
    }

    public void addFragment(Fragment fragment) {
        fragList.add(fragment);
    }

}
