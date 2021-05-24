package com.itba.runningMate.map;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

public class MapInViewPager extends MapView {

//    https://stackoverflow.com/questions/17661084/android-maps-dont-scroll-horizontally
//    https://stackoverflow.com/questions/61023565/google-map-viewpager2-swiping-conflict
//    https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/layouts/CustomMapView.java

    boolean intercept = false;
    double slideBorder = 0.1;

    public MapInViewPager(Context context) {
        super(context);
    }

    public MapInViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MapInViewPager(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public MapInViewPager(Context context, GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                intercept = ev.getX() > getScreenWidth() * slideBorder && ev.getX() < getScreenWidth() * (1 - slideBorder);
                break;
            case MotionEvent.ACTION_MOVE:
                this.getParent().requestDisallowInterceptTouchEvent(intercept);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}