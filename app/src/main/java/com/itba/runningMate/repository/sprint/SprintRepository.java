package com.itba.runningMate.repository.sprint;

import com.itba.runningMate.domain.Sprint;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public interface SprintRepository {

    Flowable<List<Sprint>> getSprint();

    Single<Sprint> getSprint(final int uid);

    void insertSprint(Sprint sprint);

    void deleteSprint(Sprint sprint);
}
