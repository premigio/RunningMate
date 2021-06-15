package com.itba.runningMate.services.location;

import com.itba.runningMate.services.location.listeners.OnTrackingLocationUpdateListener;
import com.itba.runningMate.services.location.listeners.OnTrackingMetricsUpdateListener;
import com.itba.runningMate.services.location.listeners.OnTrackingUpdateListener;
import com.itba.runningMate.utils.functional.Function;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TrackingLocationUpdatesDispatcherImpl implements TrackingLocationUpdatesDispatcher {

    public List<WeakReference<OnTrackingMetricsUpdateListener>> onTrackingMetricsUpdateListeners;
    public List<WeakReference<OnTrackingLocationUpdateListener>> onTrackingLocationsUpdateListeners;

    public TrackingLocationUpdatesDispatcherImpl() {
        onTrackingLocationsUpdateListeners = new LinkedList<>();
        onTrackingMetricsUpdateListeners = new LinkedList<>();
    }

    public void setOnTrackingUpdateListener(OnTrackingUpdateListener listener) {
        setOnTrackingLocationUpdateListener(listener);
        setOnTrackingMetricsUpdateListener(listener);
    }

    public void setOnTrackingLocationUpdateListener(OnTrackingLocationUpdateListener listener) {
        onTrackingLocationsUpdateListeners.add(new WeakReference<>(listener));
    }

    public void setOnTrackingMetricsUpdateListener(OnTrackingMetricsUpdateListener listener) {
        onTrackingMetricsUpdateListeners.add(new WeakReference<>(listener));
    }

    public void removeTrackingUpdateListener(OnTrackingUpdateListener listener) {
        removeTrackingLocationUpdateListener(listener);
        removeTrackingMetricsUpdateListener(listener);
    }

    public void removeTrackingLocationUpdateListener(OnTrackingLocationUpdateListener listener) {
        removeListeners(onTrackingLocationsUpdateListeners, listener);
    }

    public void removeTrackingMetricsUpdateListener(OnTrackingMetricsUpdateListener listener) {
        removeListeners(onTrackingMetricsUpdateListeners, listener);
    }

    public <T> void removeListeners(List<WeakReference<T>> listeners, T listener) {
        Iterator<WeakReference<T>> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            WeakReference<T> wr = iterator.next();
            if (wr.get() == listener) {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean areMetricsUpdatesListener() {
        return areListeners(onTrackingMetricsUpdateListeners);
    }

    @Override
    public boolean areLocationUpdatesListener() {
        return areListeners(onTrackingLocationsUpdateListeners);
    }

    @Override
    public boolean areUpdatesListener() {
        return areLocationUpdatesListener() || areMetricsUpdatesListener();
    }

    private <T> boolean areListeners(List<WeakReference<T>> listeners) {
        Iterator<WeakReference<T>> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            WeakReference<T> wr = iterator.next();
            if (wr.get() == null) {
                iterator.remove();
            } else {
                return true;
            }
        }
        return false;
    }

    public <T> void callListeners(List<WeakReference<T>> listeners, Function<T> function) {
        Iterator<WeakReference<T>> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            WeakReference<T> wr = iterator.next();
            if (wr.get() == null) {
                iterator.remove();
            } else {
                function.apply(wr.get());
            }
        }
    }

    public void callbackLocationUpdate(double latitude, double longitude) {
        callListeners(onTrackingLocationsUpdateListeners, l -> l.onLocationUpdate(latitude, longitude));
    }

    public void callbackDistanceUpdate(float distance) {
        callListeners(onTrackingMetricsUpdateListeners, l -> l.onDistanceUpdate(distance));
    }

    public void callbackPaceUpdate(long pace) {
        callListeners(onTrackingMetricsUpdateListeners, l -> l.onPaceUpdate(pace));
    }

    public void callbackStopWatchUpdate(long elapsedTime) {
        callListeners(onTrackingMetricsUpdateListeners, l -> l.onStopWatchUpdate(elapsedTime));
    }

}
