package com.itba.runningMate.repository.run;

import com.itba.runningMate.domain.Run;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface RunRepository {

    Flowable<List<Run>> getRun();

    Flowable<List<Run>> getRunLazy();

    Single<Run> getRun(final long uid);

    Single<Run> getRunMetrics(final long uid);

    Single<Long> insertRun(Run run);

    Single<Double> getTotalDistance();

    Single<Long> getMaxTime();

    Single<Double> getMaxKcal();

    Single<Double> getMaxSpeed();

    void deleteRun(Run run);

    void deleteRun(long runId);

    Completable updateTitle(long runId, String title);
}
