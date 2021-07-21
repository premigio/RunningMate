package com.itba.runningMate.di

import android.content.Context
import androidx.work.WorkManager
import com.itba.runningMate.repository.achievements.AchievementsRepository
import com.itba.runningMate.repository.aggregaterunmetrics.AggregateRunMetricsStorage
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.repository.runningstate.RunningStateStorage
import com.itba.runningMate.services.location.TrackingLocationUpdatesDispatcher
import com.itba.runningMate.utils.providers.files.CacheFileProvider
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider

interface DependencyContainer {

    fun getApplicationContext(): Context

    fun getSchedulerProvider(): SchedulerProvider

    fun getCacheFileProvider(): CacheFileProvider

    fun getRunRepository(): RunRepository

    fun getRunningStateStorage(): RunningStateStorage

    fun getAggregateRunMetricsStorage(): AggregateRunMetricsStorage

    fun getTrackingLocationUpdatesDispatcher(): TrackingLocationUpdatesDispatcher

    fun getAchievementsRepository(): AchievementsRepository

    fun getWorkManager(): WorkManager

}