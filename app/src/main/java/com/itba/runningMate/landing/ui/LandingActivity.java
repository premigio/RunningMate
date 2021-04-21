package com.itba.runningMate.landing.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.itba.runningMate.Constants;
import com.itba.runningMate.R;
import com.itba.runningMate.landing.model.Route;
import com.itba.runningMate.landing.repository.LandingStateStorage;
import com.itba.runningMate.landing.repository.LandingStateStorageImpl;
import com.itba.runningMate.landing.services.location.Tracker;
import com.itba.runningMate.landing.services.location.TrackingService;

public class LandingActivity extends AppCompatActivity implements OnMapReadyCallback, LandingView, ServiceConnection {

    public static final double DEFAULT_LATITUDE = -34.606451;
    public static final double DEFAULT_LONGITUDE = -58.4396797;
    public static final int DEFAULT_ZOOM = 10;
    public static final int MY_LOCATION_ZOOM = 15;
    public static final String KEY_CENTER_CAMERA = "is_camera_centered";
    public static final String KEY_LOCATION = "location";

    private Button startButton;
    private Button stopButton;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;

    private LandingStateStorage stateStorage;
    private LandingPresenter presenter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing);

        final SharedPreferences preferences = this.getSharedPreferences(LandingStateStorage.LANDING_STATE_PREFERENCES_FILE, Context.MODE_PRIVATE);

        stateStorage = new LandingStateStorageImpl(preferences);
        presenter = new LandingPresenter(this, stateStorage);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startButton = findViewById(R.id.button_landing_start);
        stopButton = findViewById(R.id.button_landing_stop);

        startButton.setOnClickListener(l -> startTracking());
        stopButton.setOnClickListener(l -> stopTracking());
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.onViewAttached();
    }

    @Override
    protected void onStop() {
        super.onStop();

        presenter.onViewDetached();
    }

    public void launchAndAttachTrackingService() {
        Intent intent = new Intent(this, TrackingService.class);
        startService(intent);
        // The binding is asynchronous, and bindService() returns immediately without returning the IBinder to the client.
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void detachTrackingService() {
        unbindService(this);
    }

    public void requestLocationPermission() {
        boolean shouldRequestRationale1 = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean shouldRequestRationale2 = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldRequestRationale1 || shouldRequestRationale2) {
            showLocationPermissionRationale();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.PERMISSION_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.PERMISSION_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                presenter.onRequestLocationPermissionResult(true);
                return;
            } else {
                presenter.onRequestLocationPermissionResult(false);
                showLocationPermissionNotGrantedError();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showLocationPermissionRationale() {
        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.alertdialog_rationale_location_title))
                .setMessage(getText(R.string.alertdialog_rationale_location_message))
                .setPositiveButton("ok", ((dialog, which) -> ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constants.PERMISSION_LOCATION)))
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    public void showLocationPermissionNotGrantedError() {
        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.alertdialog_denied_location_title))
                .setMessage(getText(R.string.alertdialog_denied_location_message))
                .setPositiveButton("ok", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    private void startTracking() {
        presenter.startRun();
    }

    private void stopTracking() {
        presenter.stopRun();
    }

    @Override
    public void showRoute(Route route) {
        if (route.isEmpty()) {
            return;
        }
        googleMap.addPolyline(new PolylineOptions()
                .color(Color.BLUE)
                .width(8f)
                .addAll(route.getLocations()));
    }

    @Override
    public void showLocation(double latitude, double longitude) {
        showLocation(latitude, longitude, MY_LOCATION_ZOOM);
    }

    @Override
    public void showDefaultLocation() {
        showLocation(DEFAULT_LATITUDE, DEFAULT_LONGITUDE, DEFAULT_ZOOM);
    }

    private void showLocation(double latitude, double longitude, float zoom) {
        if (googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        }
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
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    @SuppressLint("MissingPermission")
    public void mapEnableMyLocation() {
        if (googleMap == null) {
            return;
        }
        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    @SuppressLint("MissingPermission")
    public void mapDisableMyLocation() {
        if (googleMap == null) {
            return;
        }
        try {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public boolean areLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // todo: bundle vs shared preferences onsavedInstance vs ondestory para guardar datos
        super.onSaveInstanceState(outState);
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
}
