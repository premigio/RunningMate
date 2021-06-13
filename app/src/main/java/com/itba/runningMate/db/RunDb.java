package com.itba.runningMate.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {RunEntity.class}, version = 3)
@TypeConverters({RunConverters.class})
public abstract class RunDb extends RoomDatabase {

    public static final String NAME = "run_db";
    private static RunDb instance;

    public abstract RunDao RunDao();
}
