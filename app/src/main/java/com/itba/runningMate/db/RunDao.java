package com.itba.runningMate.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface RunDao {

    @Query("SELECT * FROM runs ORDER BY start_time DESC")
    Flowable<List<RunEntity>> getRoutes();

    @Query("SELECT * FROM runs WHERE runs.run_id = :id")
    Single<RunEntity> getRoute(final long id);

    @Query("SELECT run_id, start_time, elapsed_time, distance, velocity, pace FROM runs WHERE runs.run_id = :id")
    Single<RunEntity> getRouteMetrics(final long id);

    @Insert
    Single<Long> insertRoute(RunEntity route);

    @Delete
    Completable deleteRoute(RunEntity route);

    @Query("DELETE FROM runs WHERE runs.run_id = :id")
    Completable deleteRoute(long id);

    @Query("SELECT run_id, start_time, elapsed_time, distance, velocity, pace FROM runs ORDER BY start_time DESC")
    Flowable<List<RunEntity>> getRoutesLazy();

}
