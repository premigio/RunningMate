package com.itba.runningMate.mainpage.fragments.running.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.button.MaterialButton;
import com.itba.runningMate.Constants;
import com.itba.runningMate.R;
import com.itba.runningMate.db.RunDb;
import com.itba.runningMate.mainpage.fragments.running.model.Route;
import com.itba.runningMate.mainpage.fragments.running.repository.RunningStateStorage;
import com.itba.runningMate.mainpage.fragments.running.repository.RunningStateStorageImpl;
import com.itba.runningMate.mainpage.fragments.running.services.location.Tracker;
import com.itba.runningMate.mainpage.fragments.running.services.location.TrackingService;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.repository.run.RunRepositoryImpl;
import com.itba.runningMate.utils.schedulers.AndroidSchedulerProvider;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static com.itba.runningMate.Constants.DEFAULT_LATITUDE;
import static com.itba.runningMate.Constants.DEFAULT_LONGITUDE;
import static com.itba.runningMate.Constants.DEFAULT_ZOOM;
import static com.itba.runningMate.Constants.MY_LOCATION_ZOOM;


public class RunningFragment extends Fragment implements OnMapReadyCallback, RunningView, ServiceConnection {

    // todo: save presenter and saveinstance fragment

    private static final DecimalFormat twoDecimalPlacesFormatter = new DecimalFormat("0.00");

    private MaterialButton startStopButton;
    private TextView stopWatch;
    private TextView distance;
    private TextView pace;

    private MapView mapView;
    private GoogleMap googleMap;

    private RunningPresenter presenter;

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


        startStopButton = view.findViewById(R.id.button_start_stop);
        distance = view.findViewById(R.id.distance);
        pace = view.findViewById(R.id.pace);
        stopWatch = view.findViewById(R.id.stopwatch);

        startStopButton.setOnLongClickListener(l -> {
            presenter.onStartStopButtonClick();
            return true;
        });
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
    public void showStopRunButton() {
        startStopButton.setText(R.string.button_running_stop);
        startStopButton.setIconResource(R.drawable.ic_pause);
    }

    @Override
    public void showStartRunButton() {
        startStopButton.setText(R.string.button_running_start);
        startStopButton.setIconResource(R.drawable.ic_play);
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

    private final GoogleMap.OnMyLocationButtonClickListener mapMyLocationButtonListener = () -> {
        presenter.centerCamera();
        return true;
    };

    public void createPresenter() {
        /*presenter = (LandingPresenter) getLastNonConfigurationInstance();
        if (presenter == null) {*/
        final SharedPreferences preferences = this.getActivity().getSharedPreferences(RunningStateStorage.LANDING_STATE_PREFERENCES_FILE, Context.MODE_PRIVATE);
        final RunningStateStorage stateStorage = new RunningStateStorageImpl(preferences);
        final SchedulerProvider schedulerProvider = new AndroidSchedulerProvider();
        final RunRepository runRepository = new RunRepositoryImpl(
                RunDb.getInstance(this.getActivity().getApplicationContext()).RunDao(),
                schedulerProvider);
        presenter = new RunningPresenter(stateStorage, runRepository, schedulerProvider, this);
        /*}*/
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
    public void updateDistance(float elapsedDistance) {
        distance.setText(twoDecimalPlacesFormatter.format(elapsedDistance));
    }

    @Override
    public void updateStopwatch(long elapsedTime) {
        stopWatch.setText(hmsTimeFormatter(elapsedTime));
    }

    @Override
    public void updatePace(long pace) {
        this.pace.setText(hmsTimeFormatter(pace));
    }

    @Override
    public void showInitialMetrics() {
        pace.setText(R.string.text_view_running_initial_pace);
        distance.setText(R.string.text_view_running_initial_distance);
        stopWatch.setText(R.string.text_view_running_initial_time);
    }

    @Override
    public void removeRoutes() {
        googleMap.clear();
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
        } catch (SecurityException e) {
            Timber.e("Exception: %s", e.getMessage());
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
            Timber.e("Exception: %s", e.getMessage());
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
            Timber.e("Exception: %s", e.getMessage());
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

    @Override
    public void showSaveRunError() {
        Toast.makeText(this.getActivity(), getText(R.string.toast_error_run_save), Toast.LENGTH_LONG).show();
    }

    @Override
    public void launchRunActivity(long runId) {
        Uri uri = new Uri.Builder().scheme("runningmate")
                .authority("run")
                .appendQueryParameter("run-id", String.valueOf(runId)).build();
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @SuppressLint("DefaultLocale")
    private String hmsTimeFormatter(long millis) {
        return String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

}
