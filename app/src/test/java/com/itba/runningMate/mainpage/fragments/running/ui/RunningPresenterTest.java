package com.itba.runningMate.mainpage.fragments.running.ui;

import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.services.location.Tracker;
import com.itba.runningMate.utils.Constants;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RunningPresenterTest {

    private RunningView view;
    private RunningStateStorage stateStorage;
    private Tracker tracker;

    private RunningPresenter presenter;

    @Before
    public void setUp() {
        view = mock(RunningView.class);
        stateStorage = mock(RunningStateStorage.class);
        tracker = mock(Tracker.class);

        presenter = new RunningPresenter(stateStorage, view);
    }

    @Test
    public void givenViewAttachedWhenLocationPermissionNotGrantedThenRequestLocationPermission() {
        when(!view.areLocationPermissionGranted()).thenReturn(false);

        presenter.onViewAttached();

        verify(view).requestLocationPermission();
    }

    @Test
    public void givenViewAttachedWhenLocationPermissionGrantedThenDoNotRequestLocationPermission() {
        when(view.areLocationPermissionGranted()).thenReturn(true);

        presenter.onViewAttached();

        verify(view, never()).requestLocationPermission();
    }

    @Test
    public void givenViewAttachedWhenLocationPermissionGrantedThenLaunchAndAttachTrackingService() {
        when(view.areLocationPermissionGranted()).thenReturn(true);

        presenter.onViewAttached();

        verify(view).launchAndAttachTrackingService();
    }

    @Test
    public void givenViewDetachedThenPersistState() {
        presenter.onViewDetached();

        verify(stateStorage).persistState();
    }


//    public void onViewDetached() {
//        stateStorage.persistState();
//        if (isTrackerAttached && view.get() != null) {
//            tracker.removeTrackingLocationUpdateListener(this);
//            view.get().detachTrackingService();
//        }
//    }

    @Test
    public void givenMapAttachedWhenLocationPermissionGrantedThenEnableMyLocationOnMap() {
        when(view.areLocationPermissionGranted()).thenReturn(true);

        presenter.onMapAttached();

        verify(view).mapEnableMyLocation();
    }

    @Test
    public void givenMapAttachedWhenLocationPermissionGrantedAndStateStorageHasLastKnownLocationThenShowLastKnownLocation() {
        when(view.areLocationPermissionGranted()).thenReturn(true);
        when(stateStorage.hasLastKnownLocation()).thenReturn(true);

        presenter.onMapAttached();

        verify(view).showLocation(stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude());
    }

    @Test
    public void givenMapAttachedWhenLocationPermissionNotGrantedThenDisableMyLocationOnMapAndShowDefaultLocation() {
        when(!view.areLocationPermissionGranted()).thenReturn(false);

        presenter.onMapAttached();

        verify(view).mapDisableMyLocation();
        verify(view).showDefaultLocation();
    }

    @Test
    public void giveTrackingServiceAttachedThenBind() {
        presenter.onTrackingServiceAttached(tracker);

        verify(tracker).setOnTrackingLocationUpdateListener(presenter);
    }

    @Test
    public void onTrackingServiceDetached() {
        presenter.onTrackingServiceDetached();
        assert presenter.getTracker() == null;
    }

    @Test
    public void givenCenterCameraThenActivateCenterCamera() {
        presenter.centerCamera();

        verify(stateStorage).setCenterCamera(true);
    }

    @Test
    public void givenCenterCameraWhenStateStorageHasLastKnownLocationThenShowCenteredLastKnownLocation() {
        when(stateStorage.hasLastKnownLocation()).thenReturn(true);

        presenter.centerCamera();

        verify(stateStorage).setCenterCamera(true);
        verify(view).showLocation(stateStorage.getLastKnownLatitude(), stateStorage.getLastKnownLongitude());
    }

    @Test
    public void givenFreeCameraThenFreeCamera() {
        presenter.freeCamera();

        verify(stateStorage).setCenterCamera(false);
    }

    @Test
    public void givenAceptedRequestLocationPermissionThenLaunchAndAttachTrackingService() {
        final boolean grantedPermission = true;

        presenter.onRequestLocationPermissionResult(grantedPermission);

        verify(view).launchAndAttachTrackingService();
        // onMapAttached();
    }

    @Test
    public void givenDenyRequestLocationPermissionThenLaunchAndAttachTrackingService() {
        final boolean grantedPermission = false;

        presenter.onRequestLocationPermissionResult(grantedPermission);

        verify(view).showLocationPermissionNotGrantedError();
    }

    @Test
    public void givenStartButtonClickedWhenLocationPermissionNotGrantedThenRequestLocationPermission() {
        when(!view.areLocationPermissionGranted()).thenReturn(false);

        presenter.onStartButtonClick();

        verify(view).requestLocationPermission();
    }

    @Test
    public void givenStartButtonClickedWhenLocationPermissionGrantedThenDoNotRequestLocationPermission() {
        when(view.areLocationPermissionGranted()).thenReturn(true);

        presenter.onStartButtonClick();

        verify(view, never()).requestLocationPermission();
    }

//    @Test
//    public void onStartButtonClick() {
//        if (view.get() == null) {
//            return;
//        }
//        if (!view.get().areLocationPermissionGranted()) {
//            view.get().requestLocationPermission();
//        } else {
//            if (isTrackerAttached && !tracker.isTracking()) {
//                tracker.startTracking();
//                view.get().launchRunningActivity();
//            }
//        }
//    }

    @Test
    public void givenLocationUpdateWhenCenteredCameraEnabledThenShowLocation() {
        final double latitude = Constants.DEFAULT_LATITUDE;
        final double longitude = Constants.DEFAULT_LONGITUDE;

        when(stateStorage.isCenterCamera()).thenReturn(true);

        presenter.onLocationUpdate(latitude, longitude);

        verify(view).showLocation(latitude, longitude);
    }

    @Test
    public void givenLocationUpdateThenSaveSaveToStateStorage() {
        final double latitude = Constants.DEFAULT_LATITUDE;
        final double longitude = Constants.DEFAULT_LONGITUDE;

        presenter.onLocationUpdate(latitude, longitude);

        verify(stateStorage).setLastKnownLocation(latitude, longitude);
    }

}