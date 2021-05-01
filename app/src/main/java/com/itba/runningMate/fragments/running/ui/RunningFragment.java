package com.itba.runningMate.fragments.running.ui;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.itba.runningMate.Constants;
import com.itba.runningMate.R;
import com.itba.runningMate.db.SprintDb;
import com.itba.runningMate.fragments.running.model.Route;
import com.itba.runningMate.fragments.running.repository.LandingStateStorage;
import com.itba.runningMate.fragments.running.repository.LandingStateStorageImpl;
import com.itba.runningMate.fragments.running.services.location.Tracker;
import com.itba.runningMate.fragments.running.services.location.TrackingService;
import com.itba.runningMate.repository.sprint.SprintRepository;
import com.itba.runningMate.repository.sprint.SprintRepositoryImpl;
import com.itba.runningMate.utils.schedulers.AndroidSchedulerProvider;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;


public class RunningFragment extends Fragment implements OnMapReadyCallback, RunningView, ServiceConnection {

    public static final double DEFAULT_LATITUDE = -34.606451;
    public static final double DEFAULT_LONGITUDE = -58.4396797;
    public static final int DEFAULT_ZOOM = 10;
    public static final int MY_LOCATION_ZOOM = 15;
    public static final String KEY_CENTER_CAMERA = "is_camera_centered";
    public static final String KEY_LOCATION = "location";

    private Button startButton;
    private Button stopButton;
    private TextView stopWatch;
    private TextView distance;
    private TextView pace;

    private MapView mapView;
    private GoogleMap googleMap;

    private RunningPresenter presenter;
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
        return inflater.inflate(R.layout.fragment_running, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createPresenter();

        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        startButton = view.findViewById(R.id.button_landing_start);
        stopButton = view.findViewById(R.id.button_landing_stop);
        distance = view.findViewById(R.id.distance);
        pace = view.findViewById(R.id.pace);
        stopWatch = view.findViewById(R.id.stopwatch);

        startButton.setOnClickListener(l -> startTracking());
        stopButton.setOnClickListener(l -> stopTracking());
    }

    public void createPresenter() {
        /*presenter = (LandingPresenter) getLastNonConfigurationInstance();
        if (presenter == null) {*/
        // todo: 'this.getActivity().getSharedPreferences' fijate que onda si hay leak
        final SharedPreferences preferences = this.getActivity().getSharedPreferences(LandingStateStorage.LANDING_STATE_PREFERENCES_FILE, Context.MODE_PRIVATE);
        final LandingStateStorage stateStorage = new LandingStateStorageImpl(preferences);
        final SchedulerProvider schedulerProvider = new AndroidSchedulerProvider();
        final SprintRepository sprintRepository = new SprintRepositoryImpl(
                SprintDb.getInstance(this.getActivity().getApplicationContext()).SprintDao(),
                schedulerProvider);
        presenter = new RunningPresenter(stateStorage, sprintRepository, this);
        /*}*/
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

    public void launchAndAttachTrackingService() {
        Intent intent = new Intent(getActivity(), TrackingService.class);
        this.getActivity().startService(intent);
        // The binding is asynchronous, and bindService() returns immediately without returning the IBinder to the client.
        this.getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
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
    }

    @Override
    public void detachTrackingService() {
        this.getActivity().unbindService(this);
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
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        }
    }

    @Override
    public void updateDistanceTextView(String elapsedDistance) {
        distance.setText(elapsedDistance);
    }

    @Override
    public void updateStopwatchTextView(String elapsedTime) {
        stopWatch.setText(elapsedTime);
    }

    @Override
    public void updatePaceTextView(String pace) {
        this.pace.setText(pace);
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
                .setPositiveButton("ok", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        final Tracker tracker = (Tracker) service;
        presenter.onTrackingServiceAttached(tracker);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        /* todo: remove observers o fijarse si se hace solo */
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // todo: bundle vs shared preferences onsavedInstance vs ondestory para guardar datos
        super.onSaveInstanceState(outState);
    }

}
