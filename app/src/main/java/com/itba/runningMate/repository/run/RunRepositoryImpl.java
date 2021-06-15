package com.itba.runningMate.repository.run;

import com.itba.runningMate.db.RunDao;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class RunRepositoryImpl implements RunRepository {

    private final RunDao runDao;
    private final SchedulerProvider scheduler;

    public RunRepositoryImpl(final RunDao runDao, final SchedulerProvider scheduler) {
        this.runDao = runDao;
        this.scheduler = scheduler;
    }

    @Override
    public Flowable<List<Run>> getRun() {
        return runDao.getRoutes().map(RunMapper::toModel);
    }

    @Override
    public Flowable<List<Run>> getRunLazy() {
        return runDao.getRoutesLazy().map(RunMapper::toModel);
    }

    @Override
    public Single<Run> getRunMetrics(long uid) {
        return runDao.getRouteMetrics(uid).map(RunMapper::toModel);
    }

    @Override
    public Single<Run> getRun(long uid) {
        return runDao.getRoute(uid).map(RunMapper::toModel);
    }

    @Override
    public Single<Long> insertRun(Run run) {
        return runDao.insertRoute(RunMapper.toEntity(run));
    }

    @Override
    public Single<Double> getTotalDistance() {
        return runDao.getTotalDistance();
    }

    @Override
    public Single<Long> getMaxTime() {
        return runDao.getMaxTime();
    }

    @Override
    public Single<Double> getMaxKcal() {
        return runDao.getMaxKcal();
    }

    @Override
    public Single<Double> getMaxSpeed() {
        return runDao.getMaxSpeed();
    }

    @Override
    public Completable updateTitle(long runId, String title) {
        return runDao.updateTitle(runId, title);
    }

    @Override
    public void deleteRun(Run run) {
        runDao.deleteRoute(RunMapper.toEntity(run))
                .onErrorComplete()
                .subscribeOn(scheduler.io())
                .observeOn(scheduler.ui())
                .subscribe();
    }


    public void deleteRun(long runId) {
        runDao.deleteRoute(runId)
                .onErrorComplete()
                .subscribeOn(scheduler.io())
                .observeOn(scheduler.ui())
                .subscribe();
    }
}
