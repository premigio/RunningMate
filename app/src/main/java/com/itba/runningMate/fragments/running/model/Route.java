package com.itba.runningMate.fragments.running.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Route {

    private final List<LatLng> locations;

    public Route() {
        locations = new LinkedList<>();
    }

    public List<LatLng> getLocations() {
        return locations;
    }

    public List<Tuple<Double, Double>> getLocationsAsTuple() {
        List<Tuple<Double, Double>> aux = new LinkedList<>();
        for (LatLng location : locations) {
            aux.add(Tuple.from(location.latitude, location.longitude));
        }
        return aux;
    }

    public Route addToRoute(double latitude, double longitude) {
        locations.add(new LatLng(latitude, longitude));
        return this;
    }

    public Route addToRoute(LatLng location) {
        locations.add(location);
        return this;
    }

    public Route addTuplesToRoute(Collection<Tuple<Double, Double>> locations) {
        for (Tuple<Double, Double> tuple : locations) {
            this.locations.add(new LatLng(tuple.getA(), tuple.getB()));
        }
        return this;
    }

    public Route addLatLngsToRoute(Collection<LatLng> locations) {
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
        return locations.get(length() - 1).latitude;
    }

    public double getLastLongitude() {
        return locations.get(length() - 1).longitude;
    }

}
