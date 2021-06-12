package com.itba.runningMate.rundetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
    private TextView runTimeInterval, elapsedTime, runningTime, speed, pace, distance;
    private EditText title;

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
        title = findViewById(R.id.run_detail_title);
        runTimeInterval = findViewById(R.id.run_detail_run_time_interval);
        runningTime = findViewById(R.id.running_time);
        elapsedTime = findViewById(R.id.elapsed_time);


        //Creo el botón para volver
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Button deleteBtn = findViewById(R.id.btn_run_detail_delete);
        deleteBtn.setOnClickListener(v -> deleteConfirmationMessage());

        Button shareBtn = findViewById(R.id.btn_run_detail_share);
        shareBtn.setOnClickListener(v -> presenter.onShareButtonClick());

        title.setRawInputType(InputType.TYPE_CLASS_TEXT);
        title.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
        title.setImeOptions(EditorInfo.IME_ACTION_DONE);
        title.setOnEditorActionListener(this::onTextEditAction);
    }

    private boolean onTextEditAction(TextView textView, int actionId, KeyEvent event) {
        /* Ref: https://gist.github.com/Dogesmith/2b98df97b4fca849ff94 */
        if (event == null) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                title.clearFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                presenter.onRunTitleModified(textView.getText().toString());
            } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // Capture soft enters in other singleLine EditTexts
            } else if (actionId == EditorInfo.IME_ACTION_GO) {
            } else {
                // Let the system handle all other null KeyEvents
                return false;
            }
        } else if (actionId == EditorInfo.IME_NULL) {
           /*  Capture most soft enters in multi-line EditTexts and all hard enters;
            They supply a zero actionId and a valid keyEvent rather than
            a non-zero actionId and a null event like the previous cases.*/
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                /*We capture the event when the key is first pressed.*/
            } else {
                return true;
            }
        } else {
            /*We let the system handle it when the listener is triggered by something that
            wasn't an enter.*/
            return false;
        }
        return true;
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
    public void showTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void showRunTimeInterval(String startTime) {
        this.runTimeInterval.setText(startTime);
    }

    @Override
    public void showElapsedTime(String elapsedTime) {
        this.elapsedTime.setText(elapsedTime);
    }

    @Override
    public void showRunningTime(String runningTime) {
        this.runningTime.setText(runningTime);
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
    public void showUpdateTitleError() {
        Toast.makeText(this, "Error while attempting to update title", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDeleteError() {
        Toast.makeText(this, "Error while attempting to delete run", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}
