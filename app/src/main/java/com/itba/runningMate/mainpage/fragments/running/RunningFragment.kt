package com.itba.runningMate.mainpage.fragments.running

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itba.runningMate.R
import com.itba.runningMate.di.DependencyContainerLocator
import com.itba.runningMate.components.map.MapInViewPager
import com.itba.runningMate.services.location.Tracker
import com.itba.runningMate.services.location.TrackingService
import com.itba.runningMate.utils.Constants
import com.itba.runningMate.utils.Constants.MY_LOCATION_ZOOM

class RunningFragment : Fragment(), OnMapReadyCallback, RunningView, ServiceConnection {

    private lateinit var mapView: MapInViewPager
    private lateinit var presenter: RunningPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mainpage_running, container, false)
    }

    private val mapCameraListener = OnCameraMoveStartedListener { i: Int ->
        if (i == OnCameraMoveStartedListener.REASON_API_ANIMATION
            || i == OnCameraMoveStartedListener.REASON_GESTURE
        ) {
            presenter.freeCamera()
        }
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

    override fun launchAndAttachTrackingService() {
        val intent = Intent(activity, TrackingService::class.java)
        requireActivity().startService(intent)
        // The binding is asynchronous, and bindService() returns immediately without returning the IBinder to the client.
        requireActivity().bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    private val mapMyLocationButtonListener = OnMyLocationButtonClickListener {
        presenter.centerCamera()
        true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.PERMISSION_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                presenter.onRequestLocationPermissionResult(true)
            } else {
                presenter.onRequestLocationPermissionResult(false)
                showLocationPermissionNotGrantedError()
            }
        }
    }

    override fun detachTrackingService() {
        requireActivity().unbindService(this)
        presenter.onTrackingServiceDetached()
    }

    override fun requestLocationPermission() {
        val shouldRequestRationale1 = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val shouldRequestRationale2 = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (shouldRequestRationale1 || shouldRequestRationale2) {
            showLocationPermissionRationale()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), Constants.PERMISSION_LOCATION
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createPresenter()
        mapView = view.findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        val startButton: FloatingActionButton = view.findViewById(R.id.start)
        startButton.setOnClickListener { presenter.onStartButtonClick() }
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
    }

    @SuppressLint("MissingPermission")
    override fun mapEnableMyLocation() {
        mapView.enableMyLocation()
    }

    @SuppressLint("MissingPermission")
    override fun mapDisableMyLocation() {
        mapView.disableMyLocation()
    }

    override fun showLocationPermissionRationale() {
        AlertDialog.Builder(requireActivity())
            .setTitle(getText(R.string.alertdialog_rationale_location_title))
            .setMessage(getText(R.string.alertdialog_rationale_location_message))
            .setPositiveButton("ok") { _: DialogInterface?, _: Int ->
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    Constants.PERMISSION_LOCATION
                )
            }
            .setNegativeButton("cancel") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .create().show()
    }

    override fun showLocationPermissionNotGrantedError() {
        AlertDialog.Builder(requireActivity())
            .setTitle(getText(R.string.alertdialog_denied_location_title))
            .setMessage(getText(R.string.alertdialog_denied_location_message))
            .setNegativeButton("Dismiss") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setPositiveButton("Settings") { _: DialogInterface?, _: Int ->
                startActivity(
                    Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                )
            }
            .create().show()
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val tracker = service as Tracker
        presenter.onTrackingServiceAttached(tracker)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        presenter.onTrackingServiceDetached()
    }

    override fun areLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun launchRunningActivity() {
        val uri = Uri.Builder().scheme("runningmate")
            .authority("running")
            .build()
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun createPresenter() {
        val container = DependencyContainerLocator.locateComponent(this.requireContext())
        val stateStorage = container.getRunningStateStorage()
        presenter = RunningPresenter(stateStorage, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}