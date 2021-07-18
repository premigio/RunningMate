package com.itba.runningMate.di

import android.content.Context
import com.itba.runningMate.repository.achievements.AchievementsRepository
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

    fun getTrackingLocationUpdatesDispatcher(): TrackingLocationUpdatesDispatcher

    fun getAchievementsStorage(): AchievementsRepository

}