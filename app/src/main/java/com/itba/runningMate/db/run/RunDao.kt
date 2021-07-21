package com.itba.runningMate.db.run

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface RunDao {

    @Query("SELECT * FROM runs ORDER BY start_time DESC")
    fun getRoutes(): Flowable<List<RunEntity>>

    @Query("SELECT * FROM runs WHERE runs.run_id = :id")
    fun getRoute(id: Long): Single<RunEntity>

    @Query("SELECT run_id, start_time, end_time, elapsed_time, distance, velocity, pace, calories, title FROM runs WHERE runs.run_id = :id")
    fun getRouteMetrics(id: Long): Single<RunEntity>

    @Insert
    fun insertRoute(route: RunEntity): Single<Long>

    @Query("SELECT TOTAL(distance) FROM runs")
    fun getTotalDistance(): Maybe<Double>

    @Query("SELECT MAX(elapsed_time) FROM runs")
    fun getMaxTime(): Maybe<Long>

    @Query("SELECT MAX(calories) FROM runs")
    fun getMaxKcal(): Maybe<Double>

    @Query("SELECT MAX(velocity) FROM runs")
    fun getMaxSpeed(): Maybe<Double>

    @Delete
    fun deleteRoute(route: RunEntity): Completable

    @Query("DELETE FROM runs WHERE runs.run_id = :id")
    fun deleteRoute(id: Long): Completable

    @Query("SELECT run_id, start_time, end_time, elapsed_time, distance, velocity, pace, calories, title FROM runs ORDER BY start_time DESC")
    fun getRoutesLazy(): Flowable<List<RunEntity>>

    @Query("UPDATE runs SET title=:title WHERE run_id = :id")
    fun updateTitle(id: Long, title: String): Completable
}