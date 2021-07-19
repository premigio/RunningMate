package com.itba.runningMate.db.achievement

import androidx.room.TypeConverter
import com.itba.runningMate.domain.Achievements
import java.util.*

object AchievementConverter {

    @TypeConverter
    fun toAchievement(value: String) = enumValueOf<Achievements>(value)

    @TypeConverter
    fun fromAchievement(value: Achievements) = value.name

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}
