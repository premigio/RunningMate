package com.itba.runningMate.mainpage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.itba.runningMate.R;
import com.itba.runningMate.mainpage.fragments.pastruns.ui.PastRunsFragment;
import com.itba.runningMate.mainpage.fragments.running.ui.RunningFragment;
import com.itba.runningMate.mainpage.adapters.ViewPagerAdapter;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_menu);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // ref: https://medium.com/programming-lite/runtime-permissions-in-android-7496a5f3de55
        // todo: create un permissions manager que te maneje todo esto
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}