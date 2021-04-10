package com.itba.runningMate.landing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.itba.runningMate.Constants;
import com.itba.runningMate.R;
import com.itba.runningMate.landing.LocationService.LocationServiceBinder;


public class LandingActivity extends AppCompatActivity implements OnMapReadyCallback, OnLocationUpdateListener {

    public static final double DEFAULT_LATITUDE = -34.5997;
    public static final double DEFAULT_LONGITUDE = -58.3819;
    public static final int DEFAULT_ZOOM = 15;
    public static final int MY_LOCATION_ZOOM = 15;
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";

    private Button startButton;
    private Button stopButton;

    private LocationUpdateBroadcastReceiver locationUpdateBroadcastReceiver;
    private LocationServiceBinder locationServiceBinder;
    private ServiceConnection serviceConnection;
    private boolean serviceBounded;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private boolean centerCamara;
    /*
        estos campos me pueden servir para el caso que no este pudiendo conseguir la ultima
        ubicación, muestro esto
    */
    private LatLng lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        centerCamara = true;

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        startButton = findViewById(R.id.button_landing_start);
        stopButton = findViewById(R.id.button_landing_stop);
        locationUpdateBroadcastReceiver = new LocationUpdateBroadcastReceiver();
        serviceConnection = new LocationServiceConnection();

        startButton.setOnClickListener(l -> startTracking());
        stopButton.setOnClickListener(l -> stopTracking());

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        registerReceiver(locationUpdateBroadcastReceiver, new IntentFilter(LocationService.ACTION_LOCATION_UPDATE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        serviceBounded = false;
        unbindService(serviceConnection);
//        unregisterReceiver(locationUpdateBroadcastReceiver);
    }

    private void initLocationService() {
        if (!hasLocationPermissions()) {
            requestLocationPermission();
        } else {
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
            // The binding is asynchronous, and bindService() returns immediately without returning the IBinder to the client.
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void requestLocationPermission() {
        boolean shouldRequestRationale1 = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean shouldRequestRationale2 = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldRequestRationale1 || shouldRequestRationale2) {
            new AlertDialog.Builder(this)
                    .setTitle(getText(R.string.alertdialog_rationale_location_title))
                    .setMessage(getText(R.string.alertdialog_rationale_location_message))
                    .setPositiveButton("ok", ((dialog, which) -> ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            Constants.PERMISSION_LOCATION)))
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
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
                initLocationService();
                initGoogleMapMyLocationUI();
                getDeviceLocation();
                return;
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(getText(R.string.alertdialog_denied_location_title))
                        .setMessage(getText(R.string.alertdialog_denied_location_message))
                        .setPositiveButton("ok", (dialog, which) -> dialog.dismiss())
                        .create().show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startTracking() {
        if (!hasLocationPermissions()) {
            requestLocationPermission();
        } else {
            sendCommandTo(LocationService.class, LocationService.ACTION_START_TRACKING);
        }
    }

    private void stopTracking() {
        if (!hasLocationPermissions()) {
            requestLocationPermission();
        } else {
            sendCommandTo(LocationService.class, LocationService.ACTION_STOP_TRACKING);
        }
    }

    private void sendCommandTo(Class<?> clazz, String action) {
        Intent intent = new Intent(this, clazz);
        intent.setAction(action);
        startService(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (!hasLocationPermissions()) {
            requestLocationPermission();
            return;
        }

        initLocationService();
        initGoogleMapMyLocationUI();
        getDeviceLocation();
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        if (googleMap != null && hasLocationPermissions()) {
            LocationServices.getFusedLocationProviderClient(this)
                    .getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Location location = task.getResult();
                            lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(),
                                            location.getLongitude()), MY_LOCATION_ZOOM));
                        } else {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(DEFAULT_LATITUDE,
                                            DEFAULT_LONGITUDE), DEFAULT_ZOOM));
                        }
                    });
        } else {
            requestLocationPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void initGoogleMapMyLocationUI() {
        if (googleMap == null) {
            return;
        }
        try {
            if (hasLocationPermissions()) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.setOnCameraMoveStartedListener(i -> {
                    if (i == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION
                            || i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                        centerCamara = false;
                    }
                });
                googleMap.setOnMyLocationButtonClickListener(() -> centerCamara = true);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                requestLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationUpdate(LatLng newLocation) {
        if (serviceBounded) {
            if (centerCamara) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, MY_LOCATION_ZOOM));
            }
            if (lastKnownLocation != null && locationServiceBinder.isTracking()) {
                googleMap.addPolyline(new PolylineOptions()
                        .color(Color.BLUE)
                        .width(8f)
                        .add(lastKnownLocation)
                        .add(newLocation));
            }
        }
        lastKnownLocation = newLocation;
    }

    public class LocationServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationServiceBinder = (LocationServiceBinder) service;
            serviceBounded = true;
            locationServiceBinder.setOnLocationUpdateListener(LandingActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBounded = false;
        }
    }

    private class LocationUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LocationService.ACTION_LOCATION_UPDATE)) {
                double latitude = intent.getDoubleExtra("latitude", 0f);
                double longitude = intent.getDoubleExtra("longitude", 0f);
                lastKnownLocation = new LatLng(latitude, longitude);
                if (centerCamara) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(latitude,
                                    longitude), MY_LOCATION_ZOOM));
                }
//                if (serviceBounded) {
//                    System.out.println("My locations...");
//                    System.out.println(locationServiceBinder.getLocations());
//                }
            }
        }
    }

}