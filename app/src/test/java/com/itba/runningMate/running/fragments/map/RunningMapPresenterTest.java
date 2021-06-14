package com.itba.runningMate.running.fragments.map;

import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.services.location.Tracker;
import com.itba.runningMate.utils.Constants;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
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
        presenter.onTrackingServiceAttached(tracker);
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

//    public void onTrackingServiceAttached(Tracker tracker) {
//        this.tracker = tracker;
//        this.isTrackerAttached = true;
//        tracker.setOnTrackingLocationUpdateListener(this);
//        if (tracker.isTracking() && view.get() != null) {
//            // recuperamos la ruta y actualizamos LastKnownLocation
//            Route route = tracker.queryRoute();
//            if (!route.isEmpty()) {
//                stateStorage.setLastKnownLocation(route.getLastLatitude(), route.getLastLongitude());
//                view.get().showRoute(route);
//            }
//        }
//    }

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

//    @Test
//    public void givenLocationUpdateWhenTrackingThenShoeLocationUpdate() {
//        final double latitude = Constants.DEFAULT_LATITUDE;
//        final double longitude = Constants.DEFAULT_LONGITUDE;
//
//        when(tracker.isTracking()).thenReturn(true);
//        when(stateStorage.hasLastKnownLocation()).thenReturn(true);
//        Route route = new Route();
//        route.addToRoute(stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude())
//                .addToRoute(latitude, longitude);
//
//        presenter.onLocationUpdate(latitude, longitude);
//
//        verify(view).showRoute(route);
//    }

}