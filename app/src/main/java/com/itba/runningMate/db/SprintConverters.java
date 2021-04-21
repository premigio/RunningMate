package com.itba.runningMate.db;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SprintConverters {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static List<LatLng> jsonToLatLngList(String route) {
        if (route == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<LatLng>>() {
        }.getType();
        return gson.fromJson(route, listType);
    }

    @TypeConverter
    public static String LatLngListToJson(List<LatLng> route) {
        if (route == null) {
            return null;
        }
        Type type = new TypeToken<List<LatLng>>() {
        }.getType();
        return gson.toJson(route, type);
    }

}
