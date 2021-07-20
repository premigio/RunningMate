package com.itba.runningMate.db.achievement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.itba.runningMate.domain.Achievements
import java.util.*

@Entity(tableName = "achievements")
data class AchievementEntity(

//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "uid")
//    val uid: Long?,

    @PrimaryKey
    @ColumnInfo(name = "achievement")
    val achievement: Achievements,

    @ColumnInfo(name = "timestamp")
    val timestamp: Date
)
