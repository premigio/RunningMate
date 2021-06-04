package com.itba.runningMate.mainpage.fragments.running.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itba.runningMate.Constants;
import com.itba.runningMate.R;
import com.itba.runningMate.mainpage.fragments.running.repository.RunningStateStorage;
import com.itba.runningMate.mainpage.fragments.running.repository.RunningStateStorageImpl;
import com.itba.runningMate.mainpage.fragments.running.services.location.Tracker;
import com.itba.runningMate.mainpage.fragments.running.services.location.TrackingService;
import com.itba.runningMate.map.MapInViewPager;

import static com.itba.runningMate.Constants.DEFAULT_LATITUDE;
import static com.itba.runningMate.Constants.DEFAULT_LONGITUDE;
import static com.itba.runningMate.Constants.DEFAULT_ZOOM;
import static com.itba.runningMate.Constants.MY_LOCATION_ZOOM;

public class RunningFragment extends Fragment implements OnMapReadyCallback, RunningView, ServiceConnection {

    private MapInViewPager mapView;

    private RunningPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mainpage_running, container, false);
    }

    private final GoogleMap.OnCameraMoveStartedListener mapCameraListener = (i) -> {
        if (i == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION
                || i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            presenter.freeCamera();
        }
    };

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

    public void launchAndAttachTrackingService() {
        Intent intent = new Intent(getActivity(), TrackingService.class);
        this.getActivity().startService(intent);
        // The binding is asynchronous, and bindService() returns immediately without returning the IBinder to the client.
        this.getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    private final GoogleMap.OnMyLocationButtonClickListener mapMyLocationButtonListener = () -> {
        presenter.centerCamera();
        return true;
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.PERMISSION_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                presenter.onRequestLocationPermissionResult(true);
            } else {
                presenter.onRequestLocationPermissionResult(false);
                showLocationPermissionNotGrantedError();
            }
        }
    }

    @Override
    public void detachTrackingService() {
        this.getActivity().unbindService(this);
        presenter.onTrackingServiceDetached();
    }

    public void requestLocationPermission() {
        boolean shouldRequestRationale1 = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean shouldRequestRationale2 = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldRequestRationale1 || shouldRequestRationale2) {
            showLocationPermissionRationale();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.PERMISSION_LOCATION);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createPresenter();

        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        FloatingActionButton startButton = view.findViewById(R.id.start);
        startButton.setOnClickListener(l -> presenter.onStartButtonClick());
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
    }

    @Override
    @SuppressLint("MissingPermission")
    public void mapEnableMyLocation() {
        mapView.enableMyLocation();
    }

    @Override
    @SuppressLint("MissingPermission")
    public void mapDisableMyLocation() {
        mapView.disableMyLocation();
    }

    public void showLocationPermissionRationale() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getText(R.string.alertdialog_rationale_location_title))
                .setMessage(getText(R.string.alertdialog_rationale_location_message))
                .setPositiveButton("ok", ((dialog, which) -> requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.PERMISSION_LOCATION)))
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    public void showLocationPermissionNotGrantedError() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getText(R.string.alertdialog_denied_location_title))
                .setMessage(getText(R.string.alertdialog_denied_location_message))
                .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Settings", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .create().show();
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

    /*@Nullable
    @Override
    todo: Guardar instancia del presenter
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }*/

    public boolean areLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void launchRunningActivity() {
        Uri uri = new Uri.Builder().scheme("runningmate")
                .authority("running")
                .build();
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void createPresenter() {
        /*presenter = (LandingPresenter) getLastNonConfigurationInstance();
        if (presenter == null) {*/
        final SharedPreferences preferences = this.getActivity().getSharedPreferences(RunningStateStorage.LANDING_STATE_PREFERENCES_FILE, Context.MODE_PRIVATE);
        final RunningStateStorage stateStorage = new RunningStateStorageImpl(preferences);
        presenter = new RunningPresenter(stateStorage, this);
        /*}*/
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // todo: bundle vs shared preferences onsavedInstance vs ondestory para guardar datos
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}
