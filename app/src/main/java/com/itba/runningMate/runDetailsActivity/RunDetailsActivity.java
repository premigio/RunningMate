package com.itba.runningMate.runDetailsActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.itba.runningMate.R;
import com.itba.runningMate.db.SprintConverters;
import com.itba.runningMate.db.SprintDb;
import com.itba.runningMate.domain.Sprint;
import com.itba.runningMate.repository.sprint.SprintRepositoryImpl;
import com.itba.runningMate.utils.schedulers.AndroidSchedulerProvider;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class RunDetailsActivity extends AppCompatActivity implements RunDetailsView, OnMapReadyCallback {

    private static final int PADDING = 5; // padding de los puntos en el mapa
    private static final String SPRINT_ID = "sprint-id";
    private static SimpleDateFormat paceFormatter = new SimpleDateFormat("mm ss", Locale.getDefault());
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());


    private GoogleMap googleMap;
    private MapView mapView;
    private RunDetailsPresenter presenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_details);

        long id;

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String sprintIdString = uri.getQueryParameter(SPRINT_ID);
            id = Long.parseLong(sprintIdString, 10);
        }
        else { //todo: mejorar error
            id = 1;
        }

        createPresenter(id);

        mapView = findViewById(R.id.map_detail_run);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //Creo el botón para volver
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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

    private void createPresenter(long sprintId) {

        SchedulerProvider sp = new AndroidSchedulerProvider();

        presenter = new RunDetailsPresenter(this,
                new SprintRepositoryImpl(SprintDb.getInstance(
                        getApplicationContext()).SprintDao(),
                        sp), sp, sprintId);
    }

    @Override
    public void bindRunDetails(Sprint sprint) {
        setRunDetailsLabel(sprint);
        setMapPath(sprint.getRoute());
        setMapCenter(sprint.getRoute());
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

    private void setRunDetailsLabel(Sprint sprint) {
        TextView date, time, speed, pace, distance;

        speed = findViewById(R.id.run_description_speed_content);
        speed.setText(getString(R.string.speed_value, sprint.getVelocity()));

        pace = findViewById(R.id.run_description_pace_content);
        Date paceValue = SprintConverters.fromTimestamp(sprint.getPace());
        pace.setText(getString(R.string.pace_value, paceFormatter.format(paceValue)));

        distance = findViewById(R.id.run_description_distance_content);
        distance.setText(getString(R.string.distance_string, sprint.getDistance()));

        date = findViewById(R.id.run_description_start_time);
        date.setText(getString(R.string.date_title, dateFormat.format(sprint.getStartTime())));

        time = findViewById(R.id.run_description_elapsed_time_content);
        Date timeValue = SprintConverters.fromTimestamp(sprint.getElapsedTime());
        time.setText(getString(R.string.time_elapsed_value, timeFormat.format(timeValue)));
    }

    private void setMapCenter(List<LatLng> route) {

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
