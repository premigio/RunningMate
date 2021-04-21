package com.itba.runningMate.landing.services.location;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.itba.runningMate.Constants;
import com.itba.runningMate.R;
import com.itba.runningMate.landing.ui.LandingActivity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class TrackingService extends Service {

    public static final String HANDLER_THREAD_NAME = "LocationServiceHandlerThread";
    public static final int LOCATION_UPDATE_INTERVAL = 5000;
    public static final int LOCATION_UPDATE_FASTEST_INTERVAL = 1000;

    private NotificationManagerCompat notificationManager;

    private HandlerThread handlerThread;
    private Handler handler;

    private FusedLocationProviderClient fusedLocationClient;
    private WeakReference<OnLocationUpdateListener> onLocationUpdateListener;
    private LatLng lastLocation;
    private List<LatLng> trackedLocations;
    private boolean isTracking;
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            handleLocationUpdate(locationResult);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new TrackingServiceBinder(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TrackingService.class.getName(), " => Tracking service started");
        trackedLocations = new LinkedList<>();
        isTracking = false;
        notificationManager = NotificationManagerCompat.from(this);
        handlerThread = new HandlerThread(HANDLER_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        initUpLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        handlerThread.quit();
    }

    public void startTracking() {
        startForegroundService();
        handler.post(TrackingService.this::toggleTrackingService);
    }

    public void stopTracking() {
        stopForegroundService();
        handler.post(TrackingService.this::toggleTrackingService);
    }

    public boolean isTracking() {
        return isTracking;
    }

    public void setOnLocationUpdateListener(OnLocationUpdateListener listener) {
        onLocationUpdateListener = new WeakReference<>(listener);
    }

    public void removeLocationUpdateListener() {
        onLocationUpdateListener = null;
    }

    public List<LatLng> getTrackedLocations() {
        return trackedLocations;
    }

    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, LandingActivity.class);
        /*
            Agregar una accion al notificationIntent para decirle a nuestra landing activity que
            hacer. Tambien podes usar el paramentro flag del pendingIntnet para agreagar mas data
            (ej. updatear la vista no re crearla)
         */
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new NotificationCompat.Builder(this, Constants.NOTIFICATION_LOCATION_SERVICE_CHANNEL__ID)
                        .setContentTitle(getText(R.string.app_name))
                        .setContentText("Hey I'am using your location")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .build();

        startForeground(Constants.NOTIFICATION_LOCATION_SERVICE_ID, notification);
    }

    private void stopForegroundService() {
        stopForeground(true);
    }

    private void toggleTrackingService() {
        isTracking = !isTracking;
    }

    public LatLng getLastLocation() {
        return lastLocation;
    }

    private void handleLocationUpdate(@NonNull LocationResult locationResult) {
        Location location = locationResult.getLastLocation();
        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (isTracking) {
            trackedLocations.add(lastLocation);
        }
        if (onLocationUpdateListener != null) {
            onLocationUpdateListener.get().onLocationUpdate(lastLocation.latitude, lastLocation.longitude);
        }
    }

    @SuppressLint("MissingPermission")
    private void initUpLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        handler.post(() -> fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper()));
        /*
            TODO: chequear que los settings sean los apropiados
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnFailureListener( ... );
        */
    }

}
