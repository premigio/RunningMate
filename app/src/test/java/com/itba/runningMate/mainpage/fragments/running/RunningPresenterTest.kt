package com.itba.runningMate.mainpage.fragments.running

import com.itba.runningMate.repository.runningstate.RunningStateStorage
import com.itba.runningMate.services.location.Tracker
import com.itba.runningMate.utils.Constants
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class RunningPresenterTest {

    private lateinit var view: RunningView
    private lateinit var stateStorage: RunningStateStorage
    private lateinit var tracker: Tracker
    private lateinit var presenter: RunningPresenter

    @Before
    fun setUp() {
        view = Mockito.mock(RunningView::class.java)
        stateStorage = Mockito.mock(RunningStateStorage::class.java)
        tracker = Mockito.mock(Tracker::class.java)
        presenter = RunningPresenter(stateStorage, view)
    }

    @Test
    fun givenViewAttachedWhenLocationPermissionNotGrantedThenRequestLocationPermission() {
        Mockito.`when`(!view.areLocationPermissionGranted()).thenReturn(false)
        presenter.onViewAttached()
        Mockito.verify(view).requestLocationPermission()
    }

    @Test
    fun givenViewAttachedWhenLocationPermissionGrantedThenDoNotRequestLocationPermission() {
        Mockito.`when`(view.areLocationPermissionGranted()).thenReturn(true)
        presenter.onViewAttached()
        Mockito.verify(view, Mockito.never()).requestLocationPermission()
    }

    @Test
    fun givenViewAttachedWhenLocationPermissionGrantedThenLaunchAndAttachTrackingService() {
        Mockito.`when`(view.areLocationPermissionGranted()).thenReturn(true)
        presenter.onViewAttached()
        Mockito.verify(view).launchAndAttachTrackingService()
    }

    @Test
    fun givenViewDetachedThenPersistState() {
        presenter.onViewDetached()
        Mockito.verify(stateStorage).persistState()
    }

    @Test
    fun givenViewDetachedThenDetachTrackingService() {
        presenter.onTrackingServiceAttached(tracker)
        presenter.onViewDetached()
        Mockito.verify(tracker).removeTrackingLocationUpdateListener(
            presenter
        )
        Mockito.verify(view).detachTrackingService()
    }

    @Test
    fun givenMapAttachedWhenLocationPermissionGrantedThenEnableMyLocationOnMap() {
        Mockito.`when`(view.areLocationPermissionGranted()).thenReturn(true)
        presenter.onMapAttached()
        Mockito.verify(view).mapEnableMyLocation()
    }

    @Test
    fun givenMapAttachedWhenLocationPermissionGrantedAndStateStorageHasLastKnownLocationThenShowLastKnownLocation() {
        Mockito.`when`(view.areLocationPermissionGranted()).thenReturn(true)
        Mockito.`when`(stateStorage.hasLastKnownLocation()).thenReturn(true)
        presenter.onMapAttached()
        Mockito.verify(view).showLocation(
            stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude()
        )
    }

    @Test
    fun givenMapAttachedWhenLocationPermissionNotGrantedThenDisableMyLocationOnMapAndShowDefaultLocation() {
        Mockito.`when`(!view.areLocationPermissionGranted()).thenReturn(false)
        presenter.onMapAttached()
        Mockito.verify(view).mapDisableMyLocation()
        Mockito.verify(view).showDefaultLocation()
    }

    @Test
    fun giveTrackingServiceAttachedThenBind() {
        presenter.onTrackingServiceAttached(tracker)
        Mockito.verify(tracker).setOnTrackingLocationUpdateListener(
            presenter
        )
    }

    @Test
    fun onTrackingServiceDetached() {
        presenter.onTrackingServiceDetached()
        assert(presenter.tracker == null)
    }

    @Test
    fun givenCenterCameraThenActivateCenterCamera() {
        presenter.centerCamera()
        Mockito.verify(stateStorage).setCenterCamera(true)
    }

    @Test
    fun givenCenterCameraWhenStateStorageHasLastKnownLocationThenShowCenteredLastKnownLocation() {
        Mockito.`when`(stateStorage.hasLastKnownLocation()).thenReturn(true)
        presenter.centerCamera()
        Mockito.verify(stateStorage).setCenterCamera(true)
        Mockito.verify(view).showLocation(
            stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude()
        )
    }

    @Test
    fun givenFreeCameraThenFreeCamera() {
        presenter.freeCamera()
        Mockito.verify(stateStorage).setCenterCamera(false)
    }

    @Test
    fun givenAcceptedRequestLocationPermissionThenLaunchAndAttachTrackingService() {
        val grantedPermission = true
        presenter.onRequestLocationPermissionResult(grantedPermission)
        Mockito.verify(view).launchAndAttachTrackingService()
        // onMapAttached();
    }

    @Test
    fun givenDenyRequestLocationPermissionThenLaunchAndAttachTrackingService() {
        val grantedPermission = false
        presenter.onRequestLocationPermissionResult(grantedPermission)
        Mockito.verify(view).showLocationPermissionNotGrantedError()
    }

    @Test
    fun givenStartButtonClickedWhenLocationPermissionNotGrantedThenRequestLocationPermission() {
        Mockito.`when`(!view.areLocationPermissionGranted()).thenReturn(false)
        presenter.onStartButtonClick()
        Mockito.verify(view).requestLocationPermission()
    }

    @Test
    fun givenStartButtonClickedWhenLocationPermissionGrantedThenDoNotRequestLocationPermission() {
        Mockito.`when`(view.areLocationPermissionGranted()).thenReturn(true)
        presenter.onStartButtonClick()
        Mockito.verify(view, Mockito.never()).requestLocationPermission()
    }

    @Test
    fun givenStartButtonClickedWhenLocationPermissionGrantedAndTrackingServiceAttachedAndNotTrackingThenStartTracking() {
        Mockito.`when`(view.areLocationPermissionGranted()).thenReturn(true)
        Mockito.`when`(tracker.isTracking()).thenReturn(false)
        presenter.onTrackingServiceAttached(tracker)
        presenter.onStartButtonClick()
        Mockito.verify(tracker).startTracking()
    }

    @Test
    fun givenStartButtonClickedWhenLocationPermissionGrantedAndTrackingServiceAttachedAndNotTrackingThenLaunchRunningActivity() {
        Mockito.`when`(view.areLocationPermissionGranted()).thenReturn(true)
        Mockito.`when`(tracker.isTracking()).thenReturn(false)
        presenter.onTrackingServiceAttached(tracker)
        presenter.onStartButtonClick()
        Mockito.verify(view).launchRunningActivity()
    }

    @Test
    fun givenLocationUpdateWhenCenteredCameraEnabledThenShowLocation() {
        val latitude = Constants.DEFAULT_LATITUDE
        val longitude = Constants.DEFAULT_LONGITUDE
        Mockito.`when`(stateStorage.isCenterCamera()).thenReturn(true)
        presenter.onLocationUpdate(latitude, longitude)
        Mockito.verify(view).showLocation(latitude, longitude)
    }

    @Test
    fun givenLocationUpdateThenSaveSaveToStateStorage() {
        val latitude = Constants.DEFAULT_LATITUDE
        val longitude = Constants.DEFAULT_LONGITUDE
        presenter.onLocationUpdate(latitude, longitude)
        Mockito.verify(stateStorage).setLastKnownLocation(latitude, longitude)
    }
}