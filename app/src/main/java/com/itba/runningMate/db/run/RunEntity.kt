package com.itba.runningMate.db.run

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.*

@Entity(tableName = "runs")
data class RunEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "run_id")
    val uid: Long?,

    @ColumnInfo(name = "route")
    val route: List<List<LatLng>>?,

    @ColumnInfo(name = "start_time")
    val startTime: Date?,

    @ColumnInfo(name = "end_time")
    val endTime: Date?,

    @ColumnInfo(name = "elapsed_time")
    val elapsedTime: Long?,

    @ColumnInfo(name = "distance")
    val distance: Float?,

    @ColumnInfo(name = "velocity")
    val velocity: Float?,

    @ColumnInfo(name = "pace")
    val pace: Long?,

    @ColumnInfo(name = "calories")
    val calories: Int?,

    @ColumnInfo(name = "title")
    val title: String?
)