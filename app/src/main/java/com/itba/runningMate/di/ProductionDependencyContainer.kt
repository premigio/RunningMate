package com.itba.runningMate.di

import android.content.Context
import com.itba.runningMate.db.RunDao
import com.itba.runningMate.db.RunDb
import com.itba.runningMate.repository.achievements.AchievementsStorage
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
    private var achievementsStorage: AchievementsStorage? = null
    private var runRepository: RunRepository? = null
    private var runDb: RunDb? = null

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

    override fun getAchievementsStorage(): AchievementsStorage {
        if (achievementsStorage == null) {
            achievementsStorage = dependency.provideAchievementsStorage()
        }
        return achievementsStorage!!
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

    override fun getTrackingLocationUpdatesDispatcher(): TrackingLocationUpdatesDispatcher {
        if (trackingLocationUpdatesDispatcher == null) {
            trackingLocationUpdatesDispatcher =
                dependency.provideTrackingLocationUpdatesDispatcher()
        }
        return trackingLocationUpdatesDispatcher!!
    }

}