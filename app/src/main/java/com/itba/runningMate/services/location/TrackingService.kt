package com.itba.runningMate.services.location

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.itba.runningMate.R
import com.itba.runningMate.di.DependencyContainerLocator
import com.itba.runningMate.running.RunningActivity
import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener
import com.itba.runningMate.services.location.listeners.OnTrackingMetricsUpdateListener
import com.itba.runningMate.services.location.listeners.OnTrackingUpdateListener
import com.itba.runningMate.utils.Constants
import com.itba.runningMate.utils.run.RunMetrics
import timber.log.Timber
import java.util.*
import kotlin.math.roundToLong

class TrackingService : Service() {

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var serviceHandlerThread: HandlerThread
    private lateinit var serviceHandler: Handler
    private lateinit var mainHandler: Handler
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var updatesDispatcher: TrackingLocationUpdatesDispatcher
    private lateinit var trackedLocations: MutableList<MutableList<LatLng>>
    private lateinit var currentLapLocations: MutableList<LatLng>

    private var lastLocation: LatLng? = null

    private var lastTimeUpdateMillis: Long = 0

    var isTracking = false
        private set
    var elapsedDistance = 0f
        private set
    var startTimeMillis: Long = 0
        private set
    var endTimeMillis: Long = 0
        private set
    var runningMillis: Long = 0
        private set
    var pace: Long = 0
        private set

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            handleLocationUpdate(locationResult)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return TrackingServiceBinder(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val container = DependencyContainerLocator.locateComponent(this)
        updatesDispatcher = container.getTrackingLocationUpdatesDispatcher()
        isTracking = false
        notificationManager = NotificationManagerCompat.from(this)
        serviceHandlerThread =
            HandlerThread(HANDLER_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND)
        serviceHandlerThread.start()
        serviceHandler = Handler(serviceHandlerThread.looper)
        mainHandler = Handler(Looper.getMainLooper())
        initLocationUpdates()
        Timber.i("Tracking service is up")
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        serviceHandlerThread.quit()
        Timber.i("Tracking service is down")
    }

    fun startTracking() {
        startForegroundService()
        trackedLocations = mutableListOf()
        currentLapLocations = mutableListOf()
        trackedLocations.add(currentLapLocations)
        if (lastLocation != null) {
            currentLapLocations.add(lastLocation!!)
        }
        runningMillis = 0L
        lastTimeUpdateMillis = 0L
        elapsedDistance = 0f
        pace = 0L
        isTracking = true
        startTimeMillis = System.currentTimeMillis()
        endTimeMillis = startTimeMillis
        //        if (areListeners(onTrackingMetricsUpdateListeners)) {
        serviceHandler.post { stopWatch() }
        //        }
    }

    fun resumeTracking() {
        startForegroundService()
        isTracking = true
        endTimeMillis = System.currentTimeMillis()
        serviceHandler.post { stopWatch() }
    }

    fun newLap() {
        currentLapLocations = LinkedList()
        trackedLocations.add(currentLapLocations)
    }

    fun stopTracking() {
        stopForegroundService()
        isTracking = false
    }

    fun setOnTrackingUpdateListener(listener: OnTrackingUpdateListener) {
        setOnTrackingLocationUpdateListener(listener)
        setOnTrackingMetricsUpdateListener(listener)
    }

    fun setOnTrackingLocationUpdateListener(listener: OnTrackingLocationUpdateListener) {
        updatesDispatcher.setOnTrackingLocationUpdateListener(listener)
    }

    fun setOnTrackingMetricsUpdateListener(listener: OnTrackingMetricsUpdateListener) {
        updatesDispatcher.setOnTrackingMetricsUpdateListener(listener)
        if (isTracking) {
            serviceHandler.post { stopWatch() }
        }
    }

    fun removeTrackingUpdateListener(listener: OnTrackingUpdateListener) {
        updatesDispatcher.removeTrackingUpdateListener(listener)
    }

    fun removeTrackingLocationUpdateListener(listener: OnTrackingLocationUpdateListener) {
        updatesDispatcher.removeTrackingLocationUpdateListener(listener)
    }

    fun removeTrackingMetricsUpdateListener(listener: OnTrackingMetricsUpdateListener) {
        updatesDispatcher.removeTrackingMetricsUpdateListener(listener)
    }

    fun getTrackedLocations(): List<List<LatLng>> {
        return trackedLocations
    }

    private fun startForegroundService() {
        val notificationIntent = Intent(this, RunningActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(notificationIntent)
        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification =
            NotificationCompat.Builder(this, Constants.NOTIFICATION_TRACKING_SERVICE_CHANNEL_ID)
                .setContentText(getText(R.string.notification_tracking_service))
                .setSmallIcon(R.drawable.ic_stat_notify_tracking_service)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .build()
        startForeground(Constants.NOTIFICATION_TRACKING_SERVICE_ID, notification)
    }

    private fun stopForegroundService() {
        stopForeground(true)
    }

    private fun handleLocationUpdate(locationResult: LocationResult) {
        val location = locationResult.lastLocation
        if (lastLocation != null && areEqualLocations(
                lastLocation!!.latitude,
                lastLocation!!.longitude,
                location.latitude,
                location.longitude
            )
        ) {
            return
        }
        lastLocation = LatLng(location.latitude, location.longitude)
        if (isTracking) {
            currentLapLocations.add(lastLocation!!)
            if (currentLapLocations.size >= 2) {
                val prev = currentLapLocations[currentLapLocations.size - 2]
                elapsedDistance += RunMetrics.calculateDistance(
                    prev.latitude,
                    prev.longitude,
                    location.latitude,
                    location.longitude
                )
                pace = RunMetrics.calculatePace(elapsedDistance, runningMillis)
                updatesDispatcher.callbackDistanceUpdate(elapsedDistance)
                updatesDispatcher.callbackPaceUpdate(pace)
            }
        }
        updatesDispatcher.callbackLocationUpdate(
            lastLocation!!.latitude,
            lastLocation!!.longitude
        )
    }

    @SuppressLint("MissingPermission")
    private fun initLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.create()
        locationRequest.interval = Constants.LOCATION_UPDATE_INTERVAL.toLong()
        locationRequest.fastestInterval = Constants.LOCATION_UPDATE_FASTEST_INTERVAL.toLong()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopWatch() {
        if (isTracking && updatesDispatcher.areMetricsUpdatesListener()) {
            val currentMillis = System.currentTimeMillis()
            runningMillis += currentMillis - endTimeMillis
            endTimeMillis = currentMillis
            /*
                lastTimeUpdateMillis is the time of the last time update,
                we just want to send updates when a second has elapsed
            */if (runningMillis >= lastTimeUpdateMillis + 1000L) {
                mainHandler.post { updatesDispatcher.callbackStopWatchUpdate(runningMillis) }
                lastTimeUpdateMillis += 1000L
            }
            serviceHandler.postDelayed({ stopWatch() }, Constants.STOP_WATCH_UPDATE_INTERVAL)
        }
    }

    private fun areEqualLocations(
        latitudeA: Double,
        longitudeA: Double,
        latitudeB: Double,
        longitudeB: Double
    ): Boolean {
        var latitudeAAux = (latitudeA * 100000.0).roundToLong().toDouble() / 100000.0
        var longitudeAAux = (longitudeA * 100000.0).roundToLong().toDouble() / 100000.0
        var latitudeBAux = (latitudeB * 100000.0).roundToLong().toDouble() / 100000.0
        var longitudeBAux = (longitudeB * 100000.0).roundToLong().toDouble() / 100000.0
        return latitudeA.compareTo(latitudeB) == 0 && longitudeA.compareTo(longitudeB) == 0
    }

    companion object {
        private const val HANDLER_THREAD_NAME = "LocationServiceHandlerThread"
    }

}