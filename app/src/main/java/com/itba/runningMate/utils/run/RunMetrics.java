package com.itba.runningMate.utils.run;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RunMetrics {

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
        return Math.round(distance * 10000.0) / 10000.0f;
    }

    /* Km/H */
    public static float calculateVelocity(float distanceKm, long timeMillis) {
        return (distanceKm * (18000 / 5f)) / (float) (timeMillis / 1000);
    }

    /* Ms */
    public static long calculatePace(float distanceKm, long timeMillis) {
        return (long) (distanceKm != 0F ? (timeMillis / distanceKm) : 0L);
    }

    public static int calculateCalories(float distanceKm) {
        /*
         * https://fitness.stackexchange.com/questions/15608/energy-expenditure-calories-burned-equation-for-running/25564#25564
         * calories burned = distance run (kilometres) x weight of runner (kilograms) x 1.036
         * */
        /*
         * Broadly speaking, the estimate of 62 calories oer kilometer is more like a minimum of what
         * you can expect to burn while running.
         * ref: coachmag.co.uk */
        return Math.round(distanceKm * 62.0F);
    }

}
