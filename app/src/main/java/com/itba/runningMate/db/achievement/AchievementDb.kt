package com.itba.runningMate.db.achievement

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AchievementEntity::class], version = 1)
@TypeConverters(AchievementConverter::class)
abstract class AchievementDb : RoomDatabase() {

    abstract fun AchievementDao(): AchievementDao

    companion object {
        const val NAME = "achievement_db"
    }
}
