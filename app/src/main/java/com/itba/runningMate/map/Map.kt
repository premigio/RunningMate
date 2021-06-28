package com.itba.runningMate.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.itba.runningMate.domain.Route
import com.itba.runningMate.utils.Constants.DEFAULT_LATITUDE
import com.itba.runningMate.utils.Constants.DEFAULT_LONGITUDE
import com.itba.runningMate.utils.Constants.DEFAULT_ZOOM
import com.itba.runningMate.utils.Constants.MY_LOCATION_ZOOM

open class Map : MapView {
    private var googleMap: GoogleMap? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, i: Int) : super(
        context,
        attributeSet,
        i
    )

    constructor(context: Context, googleMapOptions: GoogleMapOptions) : super(
        context,
        googleMapOptions
    )

    fun bind(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    @JvmOverloads
    fun showLocation(
        latitude: Double,
        longitude: Double,
        zoom: Float = MY_LOCATION_ZOOM.toFloat()
    ) {
        if (googleMap != null) {
            googleMap!!.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude, longitude),
                    zoom
                )
            )
        }
    }

    fun showDefaultLocation() {
        showLocation(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, DEFAULT_ZOOM.toFloat())
    }

    fun setOnCameraMoveStartedListener(mapCameraListener: OnCameraMoveStartedListener?) {
        if (googleMap != null) {
            googleMap!!.setOnCameraMoveStartedListener(mapCameraListener)
        }
    }

    fun setOnMyLocationButtonClickListener(mapMyLocationButtonListener: OnMyLocationButtonClickListener?) {
        if (googleMap != null) {
            googleMap!!.setOnMyLocationButtonClickListener(mapMyLocationButtonListener)
        }
    }

    fun setCompassEnabled(isCompassEnabled: Boolean) {
        if (googleMap != null) {
            googleMap!!.uiSettings.isCompassEnabled = isCompassEnabled
        }
    }

    @SuppressLint("MissingPermission")
    fun enableMyLocation() {
        if (googleMap != null) {
            googleMap!!.isMyLocationEnabled = true
            googleMap!!.uiSettings.isMyLocationButtonEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    fun disableMyLocation() {
        if (googleMap != null) {
            googleMap!!.isMyLocationEnabled = false
            googleMap!!.uiSettings.isMyLocationButtonEnabled = false
        }
    }

    fun showRoute(route: Route) {
        if (route.isEmpty() || googleMap == null) {
            return
        }
        for (lap in route.getLocations()) {
            googleMap!!.addPolyline(
                PolylineOptions()
                    .color(Color.BLUE)
                    .width(8f)
                    .addAll(lap)
            )
        }
    }

    fun showRouteWithMarker(route: Route) {
        showRoute(route)
        val start = LatLng(route.firstLatitude(), route.firstLongitude())
        val end = LatLng(route.lastLatitude(), route.lastLongitude())
        googleMap!!.addMarker(
            MarkerOptions()
                .position(start)
                .icon(
                    BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
        )
        googleMap!!.addMarker(
            MarkerOptions()
                .position(end)
                .icon(
                    BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
        )
    }

    fun centerMapOn(route: Route?) {
        if (route == null || route.isEmpty()) {
            return
        }
        val boundsBuilder = LatLngBounds.builder()
        for (lap in route.getLocations()) {
            for (point in lap) {
                boundsBuilder.include(point)
            }
        }
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), PADDING))
    }

    companion object {
        private const val PADDING = 100
    }
}