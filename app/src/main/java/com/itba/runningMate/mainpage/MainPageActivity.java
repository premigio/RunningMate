package com.itba.runningMate.mainpage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.itba.runningMate.R;
import com.itba.runningMate.mainpage.adapters.ViewPagerAdapter;
import com.itba.runningMate.mainpage.fragments.feed.FeedFragment;
import com.itba.runningMate.mainpage.fragments.running.ui.RunningFragment;

import java.util.LinkedList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity {

    private List<String> titleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        titleList = new LinkedList<>();
        titleList.add(getString(R.string.run));
        titleList.add(getString(R.string.feed));

        setUpTabs();
    }

    private void setUpTabs() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new RunningFragment());
        adapter.addFragment(new FeedFragment());

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(titleList.get(position))
        ).attach();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // ref: https://medium.com/programming-lite/runtime-permissions-in-android-7496a5f3de55
        // todo: create un permissions manager que te maneje todo esto
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}