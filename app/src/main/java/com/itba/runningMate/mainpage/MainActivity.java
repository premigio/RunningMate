package com.itba.runningMate.mainpage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.itba.runningMate.R;
import com.itba.runningMate.fragments.history.PastRunsFragment;
import com.itba.runningMate.mainpage.adapters.ViewPagerAdapter;
import com.itba.runningMate.fragments.running.ui.RunningFragment;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_menu_layout);
        setUpTabs();
    }


    //todo: Averiguar si hace falta guardarse la instancia de los fragmentos
    private void setUpTabs() {
        ViewPagerAdapter adapter =
                new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(getString(R.string.run), new RunningFragment());
        adapter.addFragment(getString(R.string.past), new PastRunsFragment());
        ViewPager vp = findViewById(R.id.viewPager);
        vp.setAdapter(adapter);
        TabLayout tb = findViewById(R.id.tabLayout);
        tb.setupWithViewPager(vp);

    }
}