package com.itba.runningMate.di

import android.content.Context
import androidx.room.Room
import com.itba.runningMate.db.RunDao
import com.itba.runningMate.db.RunDb
import com.itba.runningMate.repository.achievements.AchievementsStorage
import com.itba.runningMate.repository.achievements.AchievementsStorageImpl
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.repository.run.RunRepositoryImpl
import com.itba.runningMate.repository.runningstate.RunningStateStorage
import com.itba.runningMate.repository.runningstate.RunningStateStorageImpl
import com.itba.runningMate.services.location.TrackingLocationUpdatesDispatcher
import com.itba.runningMate.services.location.TrackingLocationUpdatesDispatcherImpl
import com.itba.runningMate.utils.providers.files.CacheFileProvider
import com.itba.runningMate.utils.providers.files.CacheFileProviderImpl
import com.itba.runningMate.utils.providers.schedulers.AndroidSchedulerProvider
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider

class Dependency(context: Context) {

    val applicationContext: Context = context.applicationContext

    fun provideSchedulerProvider(): SchedulerProvider {
        return AndroidSchedulerProvider()
    }

    fun provideCacheFileProvider(): CacheFileProvider {
        return CacheFileProviderImpl(applicationContext)
    }

    fun provideRunningStateStorage(): RunningStateStorage {
        val preferences = applicationContext
            .getSharedPreferences(
                RunningStateStorage.LANDING_STATE_PREFERENCES_FILE,
                Context.MODE_PRIVATE
            )
        return RunningStateStorageImpl(preferences)
    }

    fun provideRunRepository(
        runDao: RunDao
    ): RunRepository {
        return RunRepositoryImpl(runDao)
    }

    fun provideRunDb(): RunDb {
        return Room.databaseBuilder(applicationContext, RunDb::class.java, RunDb.NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideAchievementsStorage(): AchievementsStorage {
        val preferences = applicationContext
            .getSharedPreferences(
                AchievementsStorage.ACHIEVEMENTS_PREFERENCES_FILE,
                Context.MODE_PRIVATE
            )
        return AchievementsStorageImpl(preferences)
    }

    fun provideTrackingLocationUpdatesDispatcher(): TrackingLocationUpdatesDispatcher {
        return TrackingLocationUpdatesDispatcherImpl()
    }

}