package com.itba.runningMate.rundetails;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.itba.runningMate.R;
import com.itba.runningMate.di.DependencyContainer;
import com.itba.runningMate.di.DependencyContainerLocator;
import com.itba.runningMate.domain.Route;
import com.itba.runningMate.map.Map;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.ImageProcessing;
import com.itba.runningMate.utils.providers.files.CacheFileProvider;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

public class RunDetailsActivity extends AppCompatActivity implements RunDetailsView, OnMapReadyCallback {

    private static final String RUN_ID = "run-id";

    private Map mapView;
    private TextView startDate, startTime, elapsedTime, speed, pace, distance;

    private RunDetailsPresenter presenter;

    @SuppressLint("ClickableViewAccessibility")
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

        mapView = findViewById(R.id.run_detail_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        speed = findViewById(R.id.speed);
        pace = findViewById(R.id.pace);
        distance = findViewById(R.id.distance);
        startDate = findViewById(R.id.run_detail_start_date);
        startTime = findViewById(R.id.run_detail_start_time);
        elapsedTime = findViewById(R.id.stopwatch);


        //Creo el botón para volver
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Button deleteBtn = findViewById(R.id.btn_run_detail_delete);
        deleteBtn.setOnClickListener(v -> deleteConfirmationMessage());

        Button shareBtn = findViewById(R.id.btn_run_detail_share);
        shareBtn.setOnClickListener(v -> presenter.onShareButtonClick());
    }

    private void deleteConfirmationMessage() {
        AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
        alertBox.setMessage(R.string.run_delete_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> presenter.onDeleteButtonClick())
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
        final DependencyContainer container = DependencyContainerLocator.locateComponent(this);
        final SchedulerProvider schedulerProvider = container.getSchedulerProvider();
        final RunRepository runRepository = container.getRunRepository();
        final CacheFileProvider cacheFileProvider = container.getCacheFileProvider();

        presenter = new RunDetailsPresenter(cacheFileProvider,
                runRepository,
                schedulerProvider,
                runId,
                this);
    }

    @Override
    public void endActivity() {
        finish();
    }

    @Override
    public void showSpeed(String speed) {
        this.speed.setText(speed);
    }

    @Override
    public void showPace(String pace) {
        this.pace.setText(pace);
    }

    @Override
    public void showDistance(String distance) {
        this.distance.setText(distance);
    }

    @Override
    public void showStartDate(String startDate) {
        this.startDate.setText(startDate);
    }

    @Override
    public void showStartTime(String startTime) {
        this.startTime.setText(startTime);
    }

    @Override
    public void showElapsedTime(String elapsedTime) {
        this.elapsedTime.setText(elapsedTime);
    }

    @Override
    public void showRoute(Route route) {
        mapView.showRoute(route);
        mapView.centerMapOn(route);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapView.bind(googleMap);
        presenter.onMapAttached();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_run_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_item_run_detail_share:
                presenter.onShareButtonClick();
                return true;
            case R.id.menu_item_run_detail_delete:
                deleteConfirmationMessage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareImageIntent(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    @Override
    public Bitmap getMetricsImage() {
        return ImageProcessing.createBitmapFromView(
                findViewById(R.id.run_detail_metrics),
                400,
                300
        );
    }

    @Override
    public void showShareRunError() {
        Toast.makeText(this, "Error while attempting to share run", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}
