package com.itba.runningMate.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RunEntity::class], version = 4)
@TypeConverters(RunConverters::class)
abstract class RunDb : RoomDatabase() {
    abstract fun RunDao(): RunDao

    companion object {
        const val NAME = "run_db"
    }
}