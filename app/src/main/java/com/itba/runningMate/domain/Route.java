package com.itba.runningMate.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

public class Route {

    private final List<List<LatLng>> locations;

    public Route() {
        locations = new LinkedList<>();
    }

    public static Route from(List<List<LatLng>> latLngs) {
        Route route = new Route();
        route.addLatLngsToRoute(latLngs);
        return route;
    }

    public List<List<LatLng>> getLocations() {
        return locations;
    }

    public Route addToRoute(double latitude, double longitude) {
        return addToRoute(new LatLng(latitude, longitude));
    }

    public Route addToRoute(LatLng location) {
        if (locations.isEmpty()) {
            this.locations.add(new LinkedList<>());
        }
        locations.get(length() - 1).add(location);
        return this;
    }

    public Route addLatLngsToRoute(List<List<LatLng>> locations) {
        this.locations.addAll(locations);
        return this;
    }

    public boolean isEmpty() {
        return locations.isEmpty();
    }

    public int length() {
        return locations.size();
    }

    public double getLastLatitude() {
        List<LatLng> lastLap = locations.get(length() - 1);
        return lastLap.get(lastLap.size() - 1).latitude;
    }

    public double getLastLongitude() {
        List<LatLng> lastLap = locations.get(length() - 1);
        return lastLap.get(lastLap.size() - 1).longitude;
    }

}
