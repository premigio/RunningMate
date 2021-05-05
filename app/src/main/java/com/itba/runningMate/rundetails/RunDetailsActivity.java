package com.itba.runningMate.rundetails;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.itba.runningMate.R;
import com.itba.runningMate.db.RunConverters;
import com.itba.runningMate.db.RunDb;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.run.RunRepositoryImpl;
import com.itba.runningMate.utils.schedulers.AndroidSchedulerProvider;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RunDetailsActivity extends AppCompatActivity implements RunDetailsView, OnMapReadyCallback {

    private static final int PADDING = 20; // padding de los puntos en el mapa
    private static final String RUN_ID = "run-id";
    private static SimpleDateFormat paceFormatter = new SimpleDateFormat("mm'' ss'\"'", Locale.getDefault());
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());


    private GoogleMap googleMap;
    private MapView mapView;
    private RunDetailsPresenter presenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_details);

        long id;

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String runIdString = uri.getQueryParameter(RUN_ID);
            id = Long.parseLong(runIdString, 10);
        } else { //todo: mejorar error
            id = 1;
        }

        createPresenter(id);

        mapView = findViewById(R.id.map_detail_run);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //Creo el botón para volver
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button deleteButton = findViewById(R.id.delete_button_run);
        deleteButton.setOnClickListener(this::deleteConfirmationMessage);

    }

    private void deleteConfirmationMessage(View view) {
        AlertDialog.Builder alertBox = new AlertDialog.Builder(view.getContext());
        alertBox.setMessage(R.string.run_delete_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> presenter.deleteRun())
                .setNegativeButton(R.string.no, (dialog, which) -> {
                })
                .show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

        presenter.onViewAttached();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onViewDetached();
        mapView.onStop();
    }

    private void createPresenter(long runId) {

        SchedulerProvider sp = new AndroidSchedulerProvider();

        presenter = new RunDetailsPresenter(this,
                new RunRepositoryImpl(RunDb.getInstance(
                        getApplicationContext()).RunDao(),
                        sp), sp, runId);
    }

    @Override
    public void bindRunMetrics(Run run) {
        setRunDetailsLabel(run);
    }

    @Override
    public void endActivity() {
        finish();
    }

    @Override
    public void bindRunRoute(Run run) {
        setMapPath(run.getRoute());
        setMapCenter(run.getRoute());
    }

    private void setMapPath(List<LatLng> route) {
        if (route == null || route.isEmpty()) {
            return;
        }
        googleMap.addPolyline(new PolylineOptions()
                .color(Color.BLUE)
                .width(8f)
                .addAll(route));
    }

    private void setRunDetailsLabel(Run run) {
        TextView date, time, speed, pace, distance;

        speed = findViewById(R.id.run_description_speed_content);
        speed.setText(getString(R.string.speed_value, run.getVelocity()));

        pace = findViewById(R.id.run_description_pace_content);
        Date paceValue = RunConverters.fromTimestamp(run.getPace());
        pace.setText(getString(R.string.pace_value, paceFormatter.format(paceValue)));

        distance = findViewById(R.id.run_description_distance_content);
        distance.setText(getString(R.string.distance_string, run.getDistance()));

        date = findViewById(R.id.run_description_start_time);
        date.setText(getString(R.string.date_title, dateFormat.format(run.getStartTime())));

        time = findViewById(R.id.run_description_elapsed_time_content);
        Date timeValue = RunConverters.fromTimestamp(run.getElapsedTime());
        time.setText(getString(R.string.time_elapsed_value, timeFormat.format(timeValue)));
    }

    private void setMapCenter(List<LatLng> route) {

        if (route == null || route.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (LatLng point : route) {
            boundsBuilder.include(point);
        }
        LatLngBounds bounds = boundsBuilder.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, PADDING);

        googleMap.moveCamera(cu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        presenter.onMapAttached();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
