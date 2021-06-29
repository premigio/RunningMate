package com.itba.runningMate.db

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

object RunConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun jsonToLatLngList(route: String?): List<List<LatLng>> {
        if (route == null) {
            return emptyList()
        }
        val listType = object : TypeToken<List<List<LatLng>>>() {}.type
        return gson.fromJson(route, listType)
    }

    @TypeConverter
    fun LatLngListToJson(route: List<List<LatLng>>?): String? {
        if (route == null) {
            return null
        }
        val type = object : TypeToken<List<List<LatLng>>>() {}.type
        return gson.toJson(route, type)
    }
}