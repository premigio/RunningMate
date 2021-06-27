package com.itba.runningMate.utils.run

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlin.math.roundToInt
import kotlin.math.roundToLong

object RunMetrics {

    /* Km */
    fun calculateDistance(locations: List<LatLng>?): Float {
        if (locations == null || locations.isEmpty()) {
            return (-1).toFloat()
        }
        var prev = locations[0]
        var distance = 0f
        for (i in 1 until locations.size) {
            val current = locations[i]
            val aux = FloatArray(1)
            // distancia en metros
            Location.distanceBetween(
                prev.latitude,
                prev.longitude,
                current.latitude,
                current.longitude,
                aux
            )
            distance += aux[0]
            prev = current
        }
        return distance / 1000f
    }

    /* Km */
    fun calculateDistance(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Float {
        val aux = FloatArray(1)
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, aux)
        val distance = aux[0] / 1000f
        return (distance * 10000.0).roundToLong() / 10000.0f
    }

    /* Km/H */
    fun calculateVelocity(distanceKm: Float, timeMillis: Long): Float {
        return distanceKm * (18000 / 5f) / (timeMillis / 1000).toFloat()
    }

    /* Ms */
    fun calculatePace(distanceKm: Float, timeMillis: Long): Long {
        return (if (distanceKm != 0f) (timeMillis / distanceKm).toLong() else 0L)
    }

    @JvmStatic
    fun calculateCalories(distanceKm: Float): Int {
        /*
         * https://fitness.stackexchange.com/questions/15608/energy-expenditure-calories-burned-equation-for-running/25564#25564
         * calories burned = distance run (kilometres) x weight of runner (kilograms) x 1.036
         * */
        /*
         * Broadly speaking, the estimate of 62 calories oer kilometer is more like a minimum of what
         * you can expect to burn while running.
         * ref: coachmag.co.uk */
        return (distanceKm * 62.0f).roundToInt()
    }
}