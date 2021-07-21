package com.itba.runningMate.running.fragments.map

import com.google.android.gms.maps.model.LatLng
import com.itba.runningMate.domain.Route
import com.itba.runningMate.domain.Route.Companion.from
import com.itba.runningMate.repository.runningstate.RunningStateStorage
import com.itba.runningMate.services.location.Tracker
import com.itba.runningMate.utils.Constants
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.Mockito
import org.mockito.kotlin.argumentCaptor
import java.util.*

class RunningMapPresenterTest {

    private lateinit var view: RunningMapView
    private lateinit var stateStorage: RunningStateStorage
    private lateinit var tracker: Tracker
    private lateinit var presenter: RunningMapPresenter

    @Before
    @Throws(Exception::class)
    fun setUp() {
        stateStorage = Mockito.mock(RunningStateStorage::class.java)
        view = Mockito.mock(RunningMapView::class.java)
        tracker = Mockito.mock(Tracker::class.java)
        presenter = RunningMapPresenter(stateStorage, view)
    }

    @Test
    fun givenViewAttachedThenAttachTrackingService() {
        presenter.onViewAttached()
        Mockito.verify(view).attachTrackingService()
    }

    @Test
    fun givenViewDetachedThenPersistState() {
        presenter.onViewDetached()
        Mockito.verify(stateStorage).persistState()
    }

    @Test
    fun givenViewDetachedThenRemoveTrackingLocationUpdateListener() {
        presenter.onTrackingServiceAttached(tracker)
        presenter.onViewDetached()
        Mockito.verify(tracker).removeTrackingLocationUpdateListener(
            presenter
        )
    }

    @Test
    fun givenViewDetachedThenDetachTrackingService() {
        presenter.onViewDetached()
        Mockito.verify(view).detachTrackingService()
    }

    @Test
    fun givenMapAttachedWhenStateStorageHasLastKnownLocationThenShowLastKnownLocation() {
        Mockito.`when`(stateStorage.hasLastKnownLocation()).thenReturn(true)
        presenter.onMapAttached()
        Mockito.verify(view).showLocation(
            stateStorage.getLastKnownLatitude(),
            stateStorage.getLastKnownLongitude()
        )
    }

    @Test
    fun givenMapAttachedWhenStateStorageDoNotHasLastKnownLocationThenShowDefaultLocation() {
        Mockito.`when`(stateStorage.hasLastKnownLocation()).thenReturn(false)
        presenter.onMapAttached()
        Mockito.verify(view).showDefaultLocation()
    }

    @Test
    fun givenTrackingServiceAttachedThenSetTrackingLocationUpdateListener() {
        presenter.onTrackingServiceAttached(tracker)
        Mockito.verify(tracker).setOnTrackingLocationUpdateListener(
            presenter
        )
    }

    @Test
    fun givenTrackingServiceAttachedThenShowRoute() {
        Mockito.`when`(tracker.isTracking()).thenReturn(true)
        val laps: MutableList<List<LatLng>> = LinkedList()
        val lapLocations: MutableList<LatLng> = LinkedList()
        lapLocations.add(LatLng(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE))
        lapLocations.add(LatLng(Constants.DEFAULT_LATITUDE + 1, Constants.DEFAULT_LONGITUDE + 1))
        laps.add(lapLocations)
        val route = from(laps)
        Mockito.`when`(tracker.queryRoute()).thenReturn(route)
        presenter.onTrackingServiceAttached(tracker)
        Mockito.verify(view).showRoute(route)
    }

    @Test
    fun givenTrackingServiceAttachedThenUpdateLastKnownLocation() {
        Mockito.`when`(tracker.isTracking()).thenReturn(true)
        val laps: MutableList<List<LatLng>> = LinkedList()
        val lapLocations: MutableList<LatLng> = LinkedList()
        lapLocations.add(LatLng(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE))
        lapLocations.add(LatLng(Constants.DEFAULT_LATITUDE + 1, Constants.DEFAULT_LONGITUDE + 1))
        laps.add(lapLocations)
        val route = from(laps)
        Mockito.`when`(tracker.queryRoute()).thenReturn(route)
        presenter.onTrackingServiceAttached(tracker)
        Mockito.verify(stateStorage)
            .setLastKnownLocation(route.lastLatitude(), route.lastLongitude())
    }

    @Test
    fun givenCenterCameraThenSaveStateStorage() {
        presenter.centerCamera()
        Mockito.verify(stateStorage).setCenterCamera(true)
    }

    @Test
    fun givenCenterCameraWhenStateStorageHasLastKnownLocationThenShowLastKnownLocation() {
        Mockito.`when`(stateStorage.hasLastKnownLocation()).thenReturn(true)
        presenter.centerCamera()
        Mockito.verify(view).showLocation(anyDouble(), anyDouble())
    }

    @Test
    fun givenfreeCameraThenSaveStateStorage() {
        presenter.freeCamera()
        Mockito.verify(stateStorage).setCenterCamera(false)
    }

    @Test
    fun givenLocationUpdateThenSaveStateStorage() {
        val latitude = Constants.DEFAULT_LATITUDE
        val longitude = Constants.DEFAULT_LONGITUDE
        presenter.onLocationUpdate(latitude, longitude)
        Mockito.verify(stateStorage).setLastKnownLocation(latitude, longitude)
    }

    @Test
    fun givenLocationUpdateWhenCenterCameraEnabledThenShoeLocationUpdate() {
        val latitude = Constants.DEFAULT_LATITUDE
        val longitude = Constants.DEFAULT_LONGITUDE
        Mockito.`when`(stateStorage.isCenterCamera()).thenReturn(true)
        presenter.onLocationUpdate(latitude, longitude)
        Mockito.verify(view).showLocation(latitude, longitude)
    }

    @Test
    fun givenLocationUpdateWhenTrackingThenShowLocationUpdate() {
        val latitude = Constants.DEFAULT_LATITUDE
        val longitude = Constants.DEFAULT_LONGITUDE
        Mockito.`when`(tracker.isTracking()).thenReturn(true)
        Mockito.`when`(stateStorage.hasLastKnownLocation()).thenReturn(true)
        val route = Mockito.mock(Route::class.java)
        route.addToRoute(
            stateStorage.getLastKnownLatitude(),
            stateStorage.getLastKnownLongitude()
        )

        val argumentCaptor = argumentCaptor<Route>()
        Mockito.`when`(tracker.queryRoute()).thenReturn(route)
        presenter.onTrackingServiceAttached(tracker)
        presenter.onLocationUpdate(latitude, longitude)
        Mockito.verify(view, Mockito.times(2)).showRoute(argumentCaptor.capture())
//        val capturedArgument = argumentCaptor.firstValue
//        assert(capturedArgument.lastLatitude() === latitude)
//        assert(capturedArgument.lastLongitude() === longitude)
    }

    @Test
    fun givenTrackingServiceDetachedThenSetIsTrackerAttachedFalse() {
        presenter.onTrackingServiceDetached()
        Assert.assertFalse(presenter.isTrackerAttached)
    }

    @Test
    fun givenTrackingServiceDetachedThenSetTrackerNull() {
        presenter.onTrackingServiceDetached()
        Assert.assertNull(presenter.tracker)
    }
}