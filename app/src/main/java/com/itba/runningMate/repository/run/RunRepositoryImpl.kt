package com.itba.runningMate.repository.run

import com.itba.runningMate.db.run.RunDao
import com.itba.runningMate.db.run.RunEntity
import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.run.RunMapper.Companion.toEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class RunRepositoryImpl(private val runDao: RunDao) : RunRepository {
    override fun getRun(): Flowable<List<Run>> {
        return runDao.getRoutes()
            .map { obj: List<RunEntity> -> RunMapper.toModel(obj) }
    }

    override fun getRunLazy(): Flowable<List<Run>> {
        return runDao.getRoutesLazy()
            .map { obj: List<RunEntity> -> RunMapper.toModel(obj) }
    }

    override fun getRunMetrics(uid: Long): Single<Run> {
        return runDao.getRouteMetrics(uid)
            .map { entity: RunEntity -> RunMapper.toModel(entity) }
    }

    override fun getRun(uid: Long): Single<Run> {
        return runDao.getRoute(uid)
            .map { entity: RunEntity -> RunMapper.toModel(entity) }
    }

    override fun insertRun(run: Run): Single<Long> {
        return runDao.insertRoute(toEntity(run))
    }

    override fun getTotalDistance(): Single<Double> {
        return runDao.getTotalDistance().switchIfEmpty(Single.just(0.0))
    }

    override fun getMaxTime(): Single<Long> {
        return runDao.getMaxTime().switchIfEmpty(Single.just(0))
    }

    override fun getMaxKcal(): Single<Double> {
        return runDao.getMaxKcal().switchIfEmpty(Single.just(0.0))
    }

    override fun getMaxSpeed(): Single<Double> {
        return runDao.getMaxSpeed().switchIfEmpty(Single.just(0.0))
    }

    override fun updateTitle(runId: Long, title: String): Completable {
        return runDao.updateTitle(runId, title)
    }

    override fun deleteRun(run: Run): Completable {
        return runDao.deleteRoute(toEntity(run))
    }

    override fun deleteRun(runId: Long): Completable {
        return runDao.deleteRoute(runId)
    }
}