package com.itba.runningMate.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.itba.runningMate.domain.Route;

import java.util.List;

import static com.itba.runningMate.Constants.DEFAULT_LATITUDE;
import static com.itba.runningMate.Constants.DEFAULT_LONGITUDE;
import static com.itba.runningMate.Constants.DEFAULT_ZOOM;
import static com.itba.runningMate.Constants.MY_LOCATION_ZOOM;

public class Map extends MapView {

    private static final int PADDING = 20; // padding de los puntos en el mapa

    private GoogleMap googleMap;

    public Map(Context context) {
        super(context);
    }

    public Map(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public Map(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public Map(Context context, GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }

    public void bind(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void showLocation(double latitude, double longitude, float zoom) {
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        }
    }

    public void showDefaultLocation() {
        showLocation(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, DEFAULT_ZOOM);
    }

    public void showLocation(double latitude, double longitude) {
        showLocation(latitude, longitude, MY_LOCATION_ZOOM);
    }

    public void setOnCameraMoveStartedListener(GoogleMap.OnCameraMoveStartedListener mapCameraListener) {
        if (googleMap != null) {
            googleMap.setOnCameraMoveStartedListener(mapCameraListener);
        }
    }

    public void setOnMyLocationButtonClickListener(GoogleMap.OnMyLocationButtonClickListener mapMyLocationButtonListener) {
        if (googleMap != null) {
            googleMap.setOnMyLocationButtonClickListener(mapMyLocationButtonListener);
        }
    }

    public void setCompassEnabled(boolean isCompassEnabled) {
        if (googleMap != null) {
            googleMap.getUiSettings().setCompassEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    public void enableMyLocation() {
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    public void disableMyLocation() {
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    public void showRoute(Route route) {
        if (route.isEmpty() || googleMap == null) {
            return;
        }
        for (List<LatLng> lap : route.getLocations()) {
            googleMap.addPolyline(new PolylineOptions()
                    .color(Color.BLUE)
                    .width(8f)
                    .addAll(lap));
        }
    }

    public void centerMapOn(Route route) {
        if (route == null || route.isEmpty()) {
            return;
        }

        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (List<LatLng> lap : route.getLocations()) {
            for (LatLng point : lap) {
                boundsBuilder.include(point);
            }
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), PADDING));
    }
}
