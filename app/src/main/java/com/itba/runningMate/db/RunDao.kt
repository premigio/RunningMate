package com.itba.runningMate.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface RunDao {

    @Query("SELECT * FROM runs ORDER BY start_time DESC")
    fun getRoutes(): StateFlow<List<RunEntity>>

    @Query("SELECT * FROM runs WHERE runs.run_id = :id")
    fun getRoute(id: Long): StateFlow<RunEntity?>

    @Query("SELECT run_id, start_time, end_time, elapsed_time, distance, velocity, pace, calories, title FROM runs WHERE runs.run_id = :id")
    fun getRouteMetrics(id: Long): StateFlow<RunEntity?>

    @Insert
    suspend fun insertRoute(route: RunEntity): Long?

    @Query("SELECT SUM(distance) FROM runs")
    fun getTotalDistance(): StateFlow<Double?>

    @Query("SELECT MAX(elapsed_time) FROM runs")
    suspend fun getMaxTime(): Long?

    @Query("SELECT MAX(calories) FROM runs")
    suspend fun getMaxKcal(): Double?

    @Query("SELECT MAX(velocity) FROM runs")
    suspend fun getMaxSpeed(): Double?

    @Delete
    suspend fun deleteRoute(route: RunEntity): Void

    @Query("DELETE FROM runs WHERE runs.run_id = :id")
    suspend fun deleteRoute(id: Long): Void

    @Query("SELECT run_id, start_time, end_time, elapsed_time, distance, velocity, pace, calories, title FROM runs ORDER BY start_time DESC")
    fun getRoutesLazy(): StateFlow<List<RunEntity>>

    @Query("UPDATE runs SET title=:title WHERE run_id = :id")
    suspend fun updateTitle(id: Long, title: String): Void
}