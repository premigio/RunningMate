package com.itba.runningMate.repository.run;

import com.itba.runningMate.domain.Run;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public interface RunRepository {

    Flowable<List<Run>> getRun();

    Flowable<List<Run>> getRunLazy();

    Single<Run> getRun(final long uid);

    Single<Run> getRunMetrics(final long uid);

    Single<Long> insertRun(Run run);

    void deleteRun(Run run);

    void deleteRun(long runId);
}
