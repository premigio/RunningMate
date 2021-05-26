package com.itba.runningMate.running.fragments.map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.itba.runningMate.R;
import com.itba.runningMate.domain.Route;
import com.itba.runningMate.mainpage.fragments.running.repository.RunningStateStorage;
import com.itba.runningMate.mainpage.fragments.running.repository.RunningStateStorageImpl;
import com.itba.runningMate.mainpage.fragments.running.services.location.Tracker;
import com.itba.runningMate.mainpage.fragments.running.services.location.TrackingService;

import java.util.List;

import timber.log.Timber;

import static com.itba.runningMate.Constants.DEFAULT_LATITUDE;
import static com.itba.runningMate.Constants.DEFAULT_LONGITUDE;
import static com.itba.runningMate.Constants.DEFAULT_ZOOM;
import static com.itba.runningMate.Constants.MY_LOCATION_ZOOM;


public class RunningMapFragment extends Fragment implements OnMapReadyCallback, RunningMapView, ServiceConnection {

    private MapView mapView;
    private GoogleMap googleMap;

    private RunningMapPresenter presenter;
    private final GoogleMap.OnCameraMoveStartedListener mapCameraListener = (i) -> {
        if (i == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION
                || i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            presenter.freeCamera();
        }
    };
    private final GoogleMap.OnMyLocationButtonClickListener mapMyLocationButtonListener = () -> {
        presenter.centerCamera();
        return true;
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_running_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createPresenter();

        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        presenter.onViewAttached();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();

        presenter.onViewDetached();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void attachTrackingService() {
        Intent intent = new Intent(getActivity(), TrackingService.class);
        // The binding is asynchronous, and bindService() returns immediately without returning the IBinder to the client.
        this.getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void detachTrackingService() {
        this.getActivity().unbindService(this);
        presenter.onTrackingServiceDetached();
    }

    @Override
    public void showRoute(Route route) {
        if (route.isEmpty()) {
            return;
        }
        for (List<LatLng> lap : route.getLocations()) {
            googleMap.addPolyline(new PolylineOptions()
                    .color(Color.BLUE)
                    .width(8f)
                    .addAll(lap));
        }
    }

    public void createPresenter() {
        final SharedPreferences preferences = this.getActivity().getSharedPreferences(RunningStateStorage.LANDING_STATE_PREFERENCES_FILE, Context.MODE_PRIVATE);
        final RunningStateStorage stateStorage = new RunningStateStorageImpl(preferences);

        presenter = new RunningMapPresenter(stateStorage, this);
    }

    @Override
    public void showDefaultLocation() {
        showLocation(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, DEFAULT_ZOOM);
    }

    private void showLocation(double latitude, double longitude, float zoom) {
        if (googleMap != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        }
    }

    @Override
    public void showLocation(double latitude, double longitude) {
        showLocation(latitude, longitude, MY_LOCATION_ZOOM);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupGoogleMap();
        presenter.onMapAttached();
    }

    private void setupGoogleMap() {
        if (googleMap == null) {
            return;
        }
        try {
            googleMap.setOnCameraMoveStartedListener(mapCameraListener);
            googleMap.setOnMyLocationButtonClickListener(mapMyLocationButtonListener);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e) {
            Timber.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        final Tracker tracker = (Tracker) service;
        presenter.onTrackingServiceAttached(tracker);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        presenter.onTrackingServiceDetached();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}

