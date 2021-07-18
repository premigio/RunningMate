package com.itba.runningMate.di

import android.content.Context
import androidx.room.Room
import com.itba.runningMate.db.achievement.AchievementDao
import com.itba.runningMate.db.achievement.AchievementDb
import com.itba.runningMate.db.run.RunDao
import com.itba.runningMate.db.run.RunDb
import com.itba.runningMate.repository.achievements.AchievementsRepository
import com.itba.runningMate.repository.achievements.AchievementsRepositoryImpl
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

    fun provideRunRepository(runDao: RunDao): RunRepository {
        return RunRepositoryImpl(runDao)
    }

    fun provideRunDb(): RunDb {
        return Room.databaseBuilder(applicationContext, RunDb::class.java, RunDb.NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideAchievementsDb(): AchievementDb {
        return Room.databaseBuilder(
            applicationContext,
            AchievementDb::class.java,
            AchievementDb.NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideAchievementsStorage(achievementDao: AchievementDao): AchievementsRepository {
        val preferences = applicationContext
            .getSharedPreferences(
                AchievementsRepository.ACHIEVEMENTS_PREFERENCES_FILE,
                Context.MODE_PRIVATE
            )
        return AchievementsRepositoryImpl(preferences, achievementDao)
    }

    fun provideTrackingLocationUpdatesDispatcher(): TrackingLocationUpdatesDispatcher {
        return TrackingLocationUpdatesDispatcherImpl()
    }

}