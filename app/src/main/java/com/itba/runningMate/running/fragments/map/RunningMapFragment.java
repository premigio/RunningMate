package com.itba.runningMate.running.fragments.map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.itba.runningMate.R;
import com.itba.runningMate.di.DependencyContainer;
import com.itba.runningMate.di.DependencyContainerLocator;
import com.itba.runningMate.domain.Route;
import com.itba.runningMate.map.Map;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.services.location.Tracker;
import com.itba.runningMate.services.location.TrackingService;

import static com.itba.runningMate.utils.Constants.MY_LOCATION_ZOOM;

public class RunningMapFragment extends Fragment implements OnMapReadyCallback, RunningMapView, ServiceConnection {

    private Map mapView;

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
        mapView.showRoute(route);
    }

    public void createPresenter() {
        final DependencyContainer container = DependencyContainerLocator.locateComponent(this.getActivity());
        final RunningStateStorage stateStorage = container.getRunningStateStorage();

        presenter = new RunningMapPresenter(stateStorage, this);
    }

    @Override
    public void showDefaultLocation() {
        mapView.showDefaultLocation();
    }

    @Override
    public void showLocation(double latitude, double longitude) {
        mapView.showLocation(latitude, longitude, MY_LOCATION_ZOOM);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapView.bind(googleMap);
        setupGoogleMap();
        presenter.onMapAttached();
    }

    private void setupGoogleMap() {
        mapView.setOnCameraMoveStartedListener(mapCameraListener);
        mapView.setOnMyLocationButtonClickListener(mapMyLocationButtonListener);
        mapView.setCompassEnabled(true);
        mapView.enableMyLocation();
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
