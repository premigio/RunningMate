package com.itba.runningMate.repository.achievementsstorage;

import android.content.SharedPreferences;

import static com.itba.runningMate.utils.Constants.DEFAULT_LATITUDE;
import static com.itba.runningMate.utils.Constants.DEFAULT_LONGITUDE;

public class AchievementsStorageImpl  implements AchievementsStorage {

    public static final String KEY_TOTAL_DISTANCE = "total_distance";

    private Double totalDistance;

    private SharedPreferences preferences;

    public AchievementsStorageImpl(final SharedPreferences preferences) {
        this.preferences = preferences;
        totalDistance = (double) preferences.getFloat(KEY_TOTAL_DISTANCE, 0.0f);
    }

    @Override
    public void persistState() {
        SharedPreferences.Editor editor = preferences.edit();
        if (totalDistance != null) {
            editor.putFloat(KEY_TOTAL_DISTANCE, totalDistance.floatValue());
        }
        editor.apply();
    }

    @Override
    public double getTotalDistance() {
        return totalDistance;
    }

    @Override
    public void decreaseTotalDistance(double distance) {
        if (this.totalDistance - distance < 0.0)
            this.totalDistance = 0.0;
        else
            this.totalDistance -= distance;
    }

    @Override
    public void increaseTotalDistance(double distance) {
        this.totalDistance += distance;
    }
}