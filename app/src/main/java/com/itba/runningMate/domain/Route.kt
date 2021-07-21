package com.itba.runningMate.domain

import com.google.android.gms.maps.model.LatLng
import java.util.*

class Route {

    private val locations: MutableList<MutableList<LatLng>>

    fun getLocations(): List<MutableList<LatLng>> {
        return locations
    }

    fun addToRoute(latitude: Double, longitude: Double): Route {
        return addToRoute(LatLng(latitude, longitude))
    }

    fun addToRoute(location: LatLng): Route {
        if (locations.isEmpty()) {
            locations.add(LinkedList())
        }
        locations[length() - 1].add(location)
        return this
    }

    fun addLatLngsToRoute(locations: List<List<LatLng>>?): Route {
        if (locations == null) {
            return this
        }
        for (l in locations) {
            this.locations.add(l.toMutableList())
        }
        return this
    }

    fun isEmpty(): Boolean {
        return locations.isEmpty()
    }

    fun length(): Int {
        return locations.size
    }

    fun lastLatitude(): Double {
        val lastLap: List<LatLng> = locations[length() - 1]
        return lastLap[lastLap.size - 1].latitude
    }

    fun lastLongitude(): Double {
        val lastLap: List<LatLng> = locations[length() - 1]
        return lastLap[lastLap.size - 1].longitude
    }

    fun firstLatitude(): Double {
        val firstLap: List<LatLng> = locations[0]
        return firstLap[0].latitude
    }

    fun firstLongitude(): Double {
        val firstLap: List<LatLng> = locations[0]
        return firstLap[0].longitude
    }

    companion object {
        @JvmStatic
        fun from(latLngs: List<List<LatLng>>?): Route {
            val route = Route()
            route.addLatLngsToRoute(latLngs)
            return route
        }
    }

    init {
        locations = LinkedList()
    }
}