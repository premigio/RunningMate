package com.itba.runningMate.fragments.running.services.location;

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
import com.itba.runningMate.domain.Sprint;
import com.itba.runningMate.mainpage.MainActivity;
import com.itba.runningMate.utils.sprint.SprintMetrics;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import static com.itba.runningMate.Constants.LOCATION_UPDATE_FASTEST_INTERVAL;
import static com.itba.runningMate.Constants.LOCATION_UPDATE_INTERVAL;
import static com.itba.runningMate.Constants.STOP_WATCH_UPDATE_INTERVAL;

public class TrackingService extends Service {

    private static final String HANDLER_THREAD_NAME = "LocationServiceHandlerThread";

    private NotificationManagerCompat notificationManager;

    private HandlerThread serviceHandlerThread;
    private Handler serviceHandler;
    private Handler mainHandler;

    private FusedLocationProviderClient fusedLocationClient;
    private WeakReference<OnTrackingUpdateListener> onTrackingUpdateListener;

    private boolean isTracking;

    private LatLng lastLocation;
    private List<LatLng> trackedLocations;
    private float elapsedDistance;
    private long elapsedMillis;
    private long pace;
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            handleLocationUpdate(locationResult);
        }
    };
    private long startTimeMillis = 0L;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new TrackingServiceBinder(this);
    }

    private long endTimeMillis = 0L;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TrackingService.class.getName(), " => Tracking service started");
        trackedLocations = new LinkedList<>();
        elapsedMillis = 0L;
        elapsedDistance = 0f;
        pace = 0L;
        isTracking = false;
        notificationManager = NotificationManagerCompat.from(this);
        serviceHandlerThread = new HandlerThread(HANDLER_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        serviceHandlerThread.start();
        serviceHandler = new Handler(serviceHandlerThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
        initLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        serviceHandlerThread.quit();
    }

    public void startTracking() {
        startForegroundService();
        isTracking = true;
        elapsedMillis = 0L;
        elapsedDistance = 0F;
        startTimeMillis = System.currentTimeMillis();
        serviceHandler.post(this::stopWatch);
    }

    public boolean isTracking() {
        return isTracking;
    }

    public void stopTracking() {
        stopForegroundService();
        isTracking = false;
    }

    public void setOnTrackingUpdateListener(OnTrackingUpdateListener listener) {
        onTrackingUpdateListener = new WeakReference<>(listener);
    }

    public List<LatLng> getTrackedLocations() {
        return trackedLocations;
    }

    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
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

    public void removeLocationUpdateListener() {
        onTrackingUpdateListener = null;
    }

    private void handleLocationUpdate(@NonNull LocationResult locationResult) {
        Location location = locationResult.getLastLocation();
        if (lastLocation != null && areEqualLocations(lastLocation.latitude, lastLocation.longitude, location.getLatitude(), location.getLongitude())) {
            return;
        }
        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (isTracking) {
            trackedLocations.add(lastLocation);
            if (trackedLocations.size() >= 2) {
                LatLng prev = trackedLocations.get(trackedLocations.size() - 2);
                elapsedDistance += SprintMetrics.calculateDistance(prev.latitude, prev.longitude, location.getLatitude(), location.getLongitude());
                pace = SprintMetrics.calculatePace(elapsedDistance, elapsedMillis);
                callbackDistanceUpdate(elapsedDistance);
                callbackPaceUpdate(pace);
            }
        }
        callbackLocationUpdate(lastLocation.latitude, lastLocation.longitude);
    }

    private void callbackLocationUpdate(double latitude, double longitude) {
        if (onTrackingUpdateListener != null) {
            onTrackingUpdateListener.get().onLocationUpdate(latitude, longitude);
        }
    }

    private void callbackDistanceUpdate(float distance) {
        if (onTrackingUpdateListener != null) {
            onTrackingUpdateListener.get().onDistanceUpdate(distance);
        }
    }

    private void callbackPaceUpdate(long pace) {
        if (onTrackingUpdateListener != null) {
            onTrackingUpdateListener.get().onPaceUpdate(pace);
        }
    }

    private void callbackStopWatchUpdate(long elapsedTime) {
        if (onTrackingUpdateListener != null) {
            onTrackingUpdateListener.get().onStopWatchUpdate(elapsedTime);
        }
    }

    @SuppressLint("MissingPermission")
    private void initLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        /*
            TODO: chequear que los settings sean los apropiados
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnFailureListener( ... );
        */
    }

    private void stopWatch() {
        if (isTracking) {
            endTimeMillis = System.currentTimeMillis();
            elapsedMillis = endTimeMillis - startTimeMillis;
            mainHandler.post(() -> callbackStopWatchUpdate(elapsedMillis));
            serviceHandler.postDelayed(this::stopWatch, STOP_WATCH_UPDATE_INTERVAL);
        }
    }

    private boolean areEqualLocations(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        return Double.compare(latitudeA, latitudeB) == 0 && Double.compare(longitudeA, longitudeB) == 0;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public float getElapsedDistance() {
        return elapsedDistance;
    }

    public long getElapsedMillis() {
        return elapsedMillis;
    }

    public long getPace() {
        return pace;
    }

}
