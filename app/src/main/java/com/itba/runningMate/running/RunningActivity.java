package com.itba.runningMate.running;

import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.itba.runningMate.R;
import com.itba.runningMate.running.adapters.ScreenSlidePagerAdapter;
import com.itba.runningMate.running.fragments.map.RunningMapFragment;
import com.itba.runningMate.running.fragments.metrics.RunningMetricsFragment;
import com.itba.runningMate.running.transformers.ZoomOutPageTransformer;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class RunningActivity extends AppCompatActivity implements RunningView {

    private ViewPager2 viewPager;
    private SpringDotsIndicator dotsIndicator;
    private RunningPresenter presenter;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();

        createPresenter();

        setContentView(R.layout.activity_running_another);

        viewPager = findViewById(R.id.pager);
        dotsIndicator = (SpringDotsIndicator) findViewById(R.id.dots_indicator);

        setUpTabs();
    }

    public void createPresenter() {
        presenter = new RunningPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onViewAttached();
    }

    @Override
    public void onStop() {
        super.onStop();

        presenter.onViewDetached();
    }


    private void setUpTabs() {
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        pagerAdapter.addFragment(new RunningMetricsFragment());
        pagerAdapter.addFragment(new RunningMapFragment());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
        dotsIndicator.setViewPager2(viewPager);
    }

    @Override
    public void onBackPressed() {
        /* Activity ends when stop button is pressed */
    }
}
