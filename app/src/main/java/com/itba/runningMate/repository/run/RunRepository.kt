package com.itba.runningMate.repository.run

import com.itba.runningMate.domain.Run
import kotlinx.coroutines.flow.Flow

interface RunRepository {

    suspend fun getRun(): Flow<List<Run?>>

    suspend fun getRunLazy(): Flow<List<Run?>>

    suspend fun getRun(uid: Long): Flow<Run?>

    suspend fun getRunMetrics(uid: Long): Flow<Run?>

    suspend fun insertRun(run: Run): Long?

    suspend fun getTotalDistance(): Flow<Double?>

    suspend fun getMaxTime(): Long?

    suspend fun getMaxKcal(): Double?

    suspend fun getMaxSpeed(): Double?

    suspend fun deleteRun(run: Run): Void

    suspend fun deleteRun(runId: Long): Void

    suspend fun updateTitle(runId: Long, title: String): Void

}