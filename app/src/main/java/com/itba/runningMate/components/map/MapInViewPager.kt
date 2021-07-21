package com.itba.runningMate.components.map

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.gms.maps.GoogleMapOptions

class MapInViewPager : Map {

    //    https://stackoverflow.com/questions/17661084/android-maps-dont-scroll-horizontally
    //    https://stackoverflow.com/questions/61023565/google-map-viewpager2-swiping-conflict
    //    https://github.com/osmdroid/osmdroid/blob/master/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/layouts/CustomMapView.java

    private var intercept = false
    private var slideBorder = 0.1

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(
        context, attributeSet
    )

    constructor(context: Context, attributeSet: AttributeSet, i: Int) : super(
        context, attributeSet, i
    )

    constructor(context: Context, googleMapOptions: GoogleMapOptions) : super(
        context, googleMapOptions
    )

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> intercept =
                ev.x > screenWidth * slideBorder && ev.x < screenWidth * (1 - slideBorder)
            MotionEvent.ACTION_MOVE -> this.parent.requestDisallowInterceptTouchEvent(intercept)
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        val screenWidth: Int
            get() = Resources.getSystem().displayMetrics.widthPixels
    }
}