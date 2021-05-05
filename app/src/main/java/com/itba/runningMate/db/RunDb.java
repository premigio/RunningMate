package com.itba.runningMate.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {RunEntity.class}, version = 1)
@TypeConverters({RunConverters.class})
public abstract class RunDb extends RoomDatabase {

    public static final String NAME = "run_db";
    private static RunDb instance;

    public static synchronized RunDb getInstance(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), RunDb.class, NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract RunDao RunDao();
}
