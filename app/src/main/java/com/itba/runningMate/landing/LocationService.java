package com.itba.runningMate.landing;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.itba.runningMate.Constants;
import com.itba.runningMate.R;

import java.util.LinkedList;
import java.util.List;

public class LocationService extends Service {

    static final String ACTION_LOCATION_UPDATE = Constants.APPLICATION_PACKAGE + ".LOCATION_UPDATE";
    static final String ACTION_START_TRACKING = Constants.APPLICATION_PACKAGE + ".START_TRACKING";
    static final String ACTION_STOP_TRACKING = Constants.APPLICATION_PACKAGE + ".STOP_TRACKING";
    static final String HANDLER_THREAD_NAME = "LocationServiceHandlerThread";
    static final int LOCATION_UPDATE_INTERVAL = 5000;
    static final int LOCATION_UPDATE_FASTEST_INTERVAL = 1000;

    private NotificationManagerCompat notificationManager;

    private HandlerThread handlerThread;
    private LocationServiceHandler handler;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private List<LatLng> locations;
    private boolean isTracking;
    private OnLocationUpdateListener onLocationUpdateListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocationServiceBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        locations = new LinkedList<>();
        isTracking = false;
        notificationManager = NotificationManagerCompat.from(this);
        handlerThread = new HandlerThread(HANDLER_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        handler = new LocationServiceHandler(handlerThread.getLooper());
        initUpLocationUpdates();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_START_TRACKING:
                    startForegroundService();
                    handler.post(this::toggleTrackingService);
                    break;
                case ACTION_STOP_TRACKING:
                    stopForegroundService();
                    handler.post(this::toggleTrackingService);
                    break;
                default:
                    break;
            }
        }

        return START_STICKY;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        handlerThread.quit();
    }

    private void handleLocationUpdate(@NonNull LocationResult locationResult) {
        Location location = locationResult.getLastLocation();
        if (isTracking) {
            locations.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        if (onLocationUpdateListener != null) {
            onLocationUpdateListener.onLocationUpdate(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        Intent intent = new Intent(ACTION_LOCATION_UPDATE);
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());
        sendBroadcast(intent);
    }

    @SuppressLint("MissingPermission")
    protected void initUpLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                handleLocationUpdate(locationResult);
            }
        };
        handler.post(() -> fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper()));
        /*
            TODO: chequear que los settings sean los apropiados
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnFailureListener( ... );
        */
    }

    public class LocationServiceBinder extends Binder {

        public LocationServiceBinder() {
        }

        /*
            Aca van todos los metodos que yo quiero/permito que el cliente pueda utilizar
        */
        public List<LatLng> getLocations() {
            return locations;
        }

        public boolean isTracking() {
            return isTracking;
        }

        public void startTracking() {
            handler.post(LocationService.this::toggleTrackingService);
        }

        public void stopTracking() {
            handler.post(LocationService.this::toggleTrackingService);
        }

        public void setOnLocationUpdateListener(OnLocationUpdateListener listener) {
            onLocationUpdateListener = listener;
        }
    }

}
