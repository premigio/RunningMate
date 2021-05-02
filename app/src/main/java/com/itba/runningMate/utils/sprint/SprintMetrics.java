package com.itba.runningMate.utils.sprint;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class SprintMetrics {

    /* Km */
    public static float calculateDistance(List<LatLng> locations) {
        if (locations == null || locations.isEmpty()) {
            return -1;
        }
        LatLng prev = locations.get(0);
        float distance = 0;
        for (int i = 1; i < locations.size(); i++) {
            LatLng current = locations.get(i);
            float[] aux = new float[1];
            // distancia en metros
            Location.distanceBetween(prev.latitude, prev.longitude, current.latitude, current.longitude, aux);
            distance += aux[0];
            prev = current;
        }
        return distance / 1000f;
    }

    /* Km */
    public static float calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] aux = new float[1];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, aux);
        float distance = aux[0] / 1000f;
        return Math.round(distance * 100.0) / 100.0f;
    }

    /* Km/H */
    public static float calculateVelocity(float distanceKm, long timeMillis) {
        return (distanceKm * (18000 / 5f)) / (float) (timeMillis / 1000);
    }

    /* Ms */
    public static long calculatePace(float distanceKm, long timeMillis) {
        return (long) (distanceKm != 0F ? (timeMillis / distanceKm) : 0L);
    }

}
