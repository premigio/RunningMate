package com.itba.runningMate.repository.sprint;

import com.itba.runningMate.domain.Sprint;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public interface SprintRepository {

    Flowable<List<Sprint>> getRoutes();

    Single<Sprint> getRoute(final int uid);

    void insertRoute(Sprint route);

    void deleteRoute(Sprint route);
}
