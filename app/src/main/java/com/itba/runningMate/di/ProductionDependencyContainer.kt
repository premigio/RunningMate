package com.itba.runningMate.di

import android.content.Context
import com.itba.runningMate.db.achievement.AchievementDao
import com.itba.runningMate.db.achievement.AchievementDb
import com.itba.runningMate.db.run.RunDao
import com.itba.runningMate.db.run.RunDb
import com.itba.runningMate.repository.achievements.AchievementsRepository
import com.itba.runningMate.repository.aggregaterunmetrics.AggregateRunMetricsStorage
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.repository.runningstate.RunningStateStorage
import com.itba.runningMate.services.location.TrackingLocationUpdatesDispatcher
import com.itba.runningMate.utils.providers.files.CacheFileProvider
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider

class ProductionDependencyContainer(context: Context) : DependencyContainer {

    private val dependency: Dependency = Dependency(context)

    private var trackingLocationUpdatesDispatcher: TrackingLocationUpdatesDispatcher? = null
    private var schedulerProvider: SchedulerProvider? = null
    private var cacheFileProvider: CacheFileProvider? = null
    private var runningStateStorage: RunningStateStorage? = null
    private var aggregateRunMetricsStorage: AggregateRunMetricsStorage? = null
    private var achievementsRepository: AchievementsRepository? = null
    private var runRepository: RunRepository? = null
    private var runDb: RunDb? = null
    private var achievementDb: AchievementDb? = null

    override fun getApplicationContext(): Context {
        return dependency.applicationContext
    }

    override fun getSchedulerProvider(): SchedulerProvider {
        if (schedulerProvider == null) {
            schedulerProvider = dependency.provideSchedulerProvider()
        }
        return schedulerProvider!!
    }

    override fun getCacheFileProvider(): CacheFileProvider {
        if (cacheFileProvider == null) {
            cacheFileProvider = dependency.provideCacheFileProvider()
        }
        return cacheFileProvider!!
    }

    override fun getRunningStateStorage(): RunningStateStorage {
        if (runningStateStorage == null) {
            runningStateStorage = dependency.provideRunningStateStorage()
        }
        return runningStateStorage!!
    }

    override fun getAchievementsRepository(): AchievementsRepository {
        if (achievementsRepository == null) {
            achievementsRepository = dependency.provideAchievementsRepository(getAchievementDao())
        }
        return achievementsRepository!!
    }

    override fun getAggregateRunMetricsStorage(): AggregateRunMetricsStorage {
        if (aggregateRunMetricsStorage == null) {
            aggregateRunMetricsStorage = dependency.provideAggregateRunMetricsStorage()
        }
        return aggregateRunMetricsStorage!!
    }

    override fun getRunRepository(): RunRepository {
        if (runRepository == null) {
            runRepository = dependency.provideRunRepository(getRunDao())
        }
        return runRepository!!
    }

    private fun getRunDao(): RunDao {
        if (runDb == null) {
            runDb = dependency.provideRunDb()
        }
        return runDb!!.RunDao()
    }

    private fun getAchievementDao(): AchievementDao {
        if (achievementDb == null) {
            achievementDb = dependency.provideAchievementsDb()
        }
        return achievementDb!!.AchievementDao()
    }

    override fun getTrackingLocationUpdatesDispatcher(): TrackingLocationUpdatesDispatcher {
        if (trackingLocationUpdatesDispatcher == null) {
            trackingLocationUpdatesDispatcher =
                dependency.provideTrackingLocationUpdatesDispatcher()
        }
        return trackingLocationUpdatesDispatcher!!
    }

}