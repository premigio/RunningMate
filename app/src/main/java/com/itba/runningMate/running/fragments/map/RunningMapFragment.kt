package com.itba.runningMate.running.fragments.map

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.itba.runningMate.R
import com.itba.runningMate.di.DependencyContainerLocator
import com.itba.runningMate.domain.Route
import com.itba.runningMate.map.Map
import com.itba.runningMate.services.location.Tracker
import com.itba.runningMate.services.location.TrackingService
import com.itba.runningMate.utils.Constants.MY_LOCATION_ZOOM

class RunningMapFragment : Fragment(), OnMapReadyCallback, RunningMapView, ServiceConnection {

    private lateinit var mapView: Map
    private lateinit var presenter: RunningMapPresenter

    private val mapCameraListener = OnCameraMoveStartedListener { i: Int ->
        if (i == OnCameraMoveStartedListener.REASON_API_ANIMATION
            || i == OnCameraMoveStartedListener.REASON_GESTURE
        ) {
            presenter.freeCamera()
        }
    }

    private val mapMyLocationButtonListener = OnMyLocationButtonClickListener {
        presenter.centerCamera()
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_running_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createPresenter()
        mapView = view.findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        presenter.onViewAttached()
    }

    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        presenter.onViewDetached()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun attachTrackingService() {
        val intent = Intent(activity, TrackingService::class.java)
        // The binding is asynchronous, and bindService() returns immediately without returning the IBinder to the client.
        this.requireActivity().bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun detachTrackingService() {
        this.requireActivity().unbindService(this)
        presenter.onTrackingServiceDetached()
    }

    override fun showRoute(route: Route) {
        mapView.showRoute(route)
    }

    private fun createPresenter() {
        val container = DependencyContainerLocator.locateComponent(this.activity)
        val stateStorage = container.runningStateStorage
        presenter = RunningMapPresenter(stateStorage, this)
    }

    override fun showDefaultLocation() {
        mapView.showDefaultLocation()
    }

    override fun showLocation(latitude: Double, longitude: Double) {
        mapView.showLocation(latitude, longitude, MY_LOCATION_ZOOM.toFloat())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapView.bind(googleMap)
        setupGoogleMap()
        presenter.onMapAttached()
    }

    private fun setupGoogleMap() {
        mapView.setOnCameraMoveStartedListener(mapCameraListener)
        mapView.setOnMyLocationButtonClickListener(mapMyLocationButtonListener)
        mapView.setCompassEnabled(true)
        mapView.enableMyLocation()
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val tracker = service as Tracker
        presenter.onTrackingServiceAttached(tracker)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        presenter.onTrackingServiceDetached()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}