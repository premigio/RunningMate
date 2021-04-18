package com.itba.runningMate.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {SprintEntity.class}, version = 1)
@TypeConverters({SprintConverters.class})
public abstract class SprintDb extends RoomDatabase {

    public static final String NAME = "sprint_db";
    private static SprintDb instance;

    public static synchronized SprintDb getInstance(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), SprintDb.class, NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract SprintDao SprintDao();
}
