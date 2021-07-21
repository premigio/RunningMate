package com.itba.runningMate.running.fragments.map;

import com.google.android.gms.maps.model.LatLng;
import com.itba.runningMate.domain.Route;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.services.location.Tracker;
import com.itba.runningMate.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RunningMapPresenterTest {

    private RunningMapView view;
    private RunningStateStorage stateStorage;
    private Tracker tracker;

    private RunningMapPresenter presenter;

    @Before
    public void setUp() throws Exception {
        stateStorage = mock(RunningStateStorage.class);
        view = mock(RunningMapView.class);
        tracker = mock(Tracker.class);

        presenter = new RunningMapPresenter(stateStorage, view);
    }

    @Test
    public void givenViewAttachedThenAttachTrackingService() {
        presenter.onViewAttached();

        verify(view).attachTrackingService();
    }

    @Test
    public void givenViewDetachedThenPersistState() {
        presenter.onViewDetached();

        verify(stateStorage).persistState();
    }

    @Test
    public void givenViewDetachedThenRemoveTrackingLocationUpdateListener() {
        presenter.onTrackingServiceAttached(tracker);
        presenter.onViewDetached();

        verify(tracker).removeTrackingLocationUpdateListener(presenter);
    }

    @Test
    public void givenViewDetachedThenDetachTrackingService() {
        presenter.onViewDetached();

        verify(view).detachTrackingService();
    }

    @Test
    public void givenMapAttachedWhenStateStorageHasLastKnownLocationThenShowLastKnownLocation() {
        when(stateStorage.hasLastKnownLocation()).thenReturn(true);

        presenter.onMapAttached();

        verify(view).showLocation(stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude());
    }

    @Test
    public void givenMapAttachedWhenStateStorageDoNotHasLastKnownLocationThenShowDefaultLocation() {
        when(stateStorage.hasLastKnownLocation()).thenReturn(false);

        presenter.onMapAttached();

        verify(view).showDefaultLocation();
    }

    @Test
    public void givenTrackingServiceAttachedThenSetTrackingLocationUpdateListener() {
        presenter.onTrackingServiceAttached(tracker);

        verify(tracker).setOnTrackingLocationUpdateListener(presenter);
    }

    @Test
    public void givenTrackingServiceAttachedThenShowRoute() {
        when(tracker.isTracking()).thenReturn(true);
        List<List<LatLng>> laps = new LinkedList<>();
        List<LatLng> lapLocations = new LinkedList<>();
        lapLocations.add(new LatLng(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE));
        lapLocations.add(new LatLng(Constants.DEFAULT_LATITUDE + 1, Constants.DEFAULT_LONGITUDE + 1));
        laps.add(lapLocations);
        Route route = Route.from(laps);
        when(tracker.queryRoute()).thenReturn(route);

        presenter.onTrackingServiceAttached(tracker);

        verify(view).showRoute(route);
    }

    @Test
    public void givenTrackingServiceAttachedThenUpdateLastKnownLocation() {
        when(tracker.isTracking()).thenReturn(true);
        List<List<LatLng>> laps = new LinkedList<>();
        List<LatLng> lapLocations = new LinkedList<>();
        lapLocations.add(new LatLng(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE));
        lapLocations.add(new LatLng(Constants.DEFAULT_LATITUDE + 1, Constants.DEFAULT_LONGITUDE + 1));
        laps.add(lapLocations);
        Route route = Route.from(laps);
        when(tracker.queryRoute()).thenReturn(route);

        presenter.onTrackingServiceAttached(tracker);

        verify(stateStorage).setLastKnownLocation(route.getLastLatitude(), route.getLastLongitude());
    }

    @Test
    public void givenCenterCameraThenSaveStateStorage() {
        presenter.centerCamera();

        verify(stateStorage).setCenterCamera(true);
    }

    @Test
    public void givenCenterCameraWhenStateStorageHasLastKnownLocationThenShowLastKnownLocation() {
        when(stateStorage.hasLastKnownLocation()).thenReturn(true);

        presenter.centerCamera();

        verify(view).showLocation(stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude());
    }

    @Test
    public void givenfreeCameraThenSaveStateStorage() {
        presenter.freeCamera();

        verify(stateStorage).setCenterCamera(false);
    }

    @Test
    public void givenLocationUpdateThenSaveStateStorage() {
        final double latitude = Constants.DEFAULT_LATITUDE;
        final double longitude = Constants.DEFAULT_LONGITUDE;

        presenter.onLocationUpdate(latitude, longitude);

        verify(stateStorage).setLastKnownLocation(latitude, longitude);
    }

    @Test
    public void givenLocationUpdateWhenCenterCameraEnabledThenShoeLocationUpdate() {
        final double latitude = Constants.DEFAULT_LATITUDE;
        final double longitude = Constants.DEFAULT_LONGITUDE;

        when(stateStorage.isCenterCamera()).thenReturn(true);

        presenter.onLocationUpdate(latitude, longitude);

        verify(view).showLocation(latitude, longitude);
    }

    @Test
    public void givenLocationUpdateWhenTrackingThenShowLocationUpdate() {
        final double latitude = Constants.DEFAULT_LATITUDE;
        final double longitude = Constants.DEFAULT_LONGITUDE;

        when(tracker.isTracking()).thenReturn(true);
        when(stateStorage.hasLastKnownLocation()).thenReturn(true);

        Route route = mock(Route.class);
        route.addToRoute(stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude());
        ArgumentCaptor<Route> argumentCaptor = ArgumentCaptor.forClass(Route.class);

        when(tracker.queryRoute()).thenReturn(route);

        presenter.onTrackingServiceAttached(tracker);
        presenter.onLocationUpdate(latitude, longitude);

        verify(view, times(2)).showRoute(argumentCaptor.capture());
        Route capturedArgument = argumentCaptor.getValue();
        assert capturedArgument.getLastLatitude() == latitude;
        assert capturedArgument.getLastLongitude() == longitude;
    }

    @Test
    public void givenTrackingServiceDetachedThenSetIsTrackerAttachedFalse() {
        presenter.onTrackingServiceDetached();

        assertFalse(presenter.isTrackerAttached());
    }

    @Test
    public void givenTrackingServiceDetachedThenSetTrackerNull() {
        presenter.onTrackingServiceDetached();

        assertNull(presenter.getTracker());
    }

}