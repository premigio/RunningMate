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
public interface SprintDao {

    @Query("SELECT * FROM sprints ORDER BY start_time DESC")
    Flowable<List<SprintEntity>> getRoutes();

    @Query("SELECT * FROM sprints WHERE sprints.sprint_id = :id")
    Single<SprintEntity> getRoute(final long id);

    @Insert
    Completable insertRoute(SprintEntity route);

    @Delete
    Completable deleteRoute(SprintEntity route);

    // query por dia

    // query por tiempo

    // query por km

    /* Algunos ejemplos:

    @Query("SELECT * FROM user WHERE age BETWEEN :minAge AND :maxAge")
    public User[] loadAllUsersBetweenAges(int minAge, int maxAge);
    @Query("SELECT * FROM user WHERE first_name LIKE :search " + "OR last_name LIKE :search")
    public List<User> findUserWithName(String search);

    */


}
