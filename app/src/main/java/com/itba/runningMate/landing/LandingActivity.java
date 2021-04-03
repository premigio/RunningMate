package com.itba.runningMate.landing;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itba.runningMate.Constants;
import com.itba.runningMate.R;

public class LandingActivity extends AppCompatActivity {

    private Button startButton;
    private Button stopButton;
    private TextView textView;
    private LocationUpdateBroadcastReceiver locationUpdateBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        startButton = findViewById(R.id.button_landing_start);
        stopButton = findViewById(R.id.button_landing_stop);
        textView = findViewById(R.id.text);
        locationUpdateBroadcastReceiver = new LocationUpdateBroadcastReceiver();


        startButton.setOnClickListener(l -> startTracking());
        stopButton.setOnClickListener(l -> stopTracking());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            initLocationService();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(locationUpdateBroadcastReceiver, new IntentFilter(LocationService.ACTION_LOCATION_UPDATE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(locationUpdateBroadcastReceiver);
    }

    private void initLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            sendCommandTo(LocationService.class, LocationService.ACTION_START_TRACKING);
        }
    }

    private void stopTracking() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private class LocationUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LocationService.ACTION_LOCATION_UPDATE)) {
                double latitude = intent.getDoubleExtra("latitude", 0f);
                double longitude = intent.getDoubleExtra("longitude", 0f);
                textView.setText(latitude + ", " + longitude);
            }
        }
    }

}