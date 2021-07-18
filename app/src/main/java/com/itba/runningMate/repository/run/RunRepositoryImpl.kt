package com.itba.runningMate.repository.run

import com.itba.runningMate.db.RunDao
import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.run.RunMapper.Companion.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RunRepositoryImpl(private val runDao: RunDao) : RunRepository {
    override suspend fun getRun(): Flow<List<Run?>> {
        return runDao.getRoutes()
            .map { obj -> RunMapper.toModel(obj) }
    }

    override suspend fun getRunLazy(): Flow<List<Run?>> {
        return runDao.getRoutesLazy().map { r -> RunMapper.toModel(r) }
    }

    override suspend fun getRunMetrics(uid: Long): Flow<Run?> {
        val runMetrics =  runDao.getRouteMetrics(uid)
        return runMetrics.map { value -> RunMapper.toModel(value)}
    }

    override suspend fun getRun(uid: Long): Flow<Run?> {
        val run = runDao.getRoute(uid)
        return run.map{running -> RunMapper.toModel(running)}
    }

    override suspend fun insertRun(run: Run): Long? {
        return runDao.insertRoute(toEntity(run))
    }

    override suspend fun getTotalDistance(): Flow<Double?> {
        return runDao.getTotalDistance()
    }

    override suspend fun getMaxTime(): Long? {
        return runDao.getMaxTime()
    }

    override suspend fun getMaxKcal(): Double? {
        return runDao.getMaxKcal()
    }

    override suspend fun getMaxSpeed(): Double? {
        return runDao.getMaxSpeed()
    }

    override suspend fun updateTitle(runId: Long, title: String): Void {
        return runDao.updateTitle(runId, title)
    }

    override suspend fun deleteRun(run: Run): Void {
        return runDao.deleteRoute(toEntity(run))
    }

    override suspend fun deleteRun(runId: Long): Void {
        return runDao.deleteRoute(runId)
    }
}