package com.itba.runningMate.di;

import android.content.Context;

import com.itba.runningMate.achievements.elements.AchievementsElementView;
import com.itba.runningMate.db.RunDao;
import com.itba.runningMate.db.RunDb;
import com.itba.runningMate.repository.achievementsstorage.AchievementsStorage;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.providers.files.CacheFileProvider;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

public class ProductionDependencyContainer implements DependencyContainer {

    private final Dependency dependency;

    private SchedulerProvider schedulerProvider;
    private CacheFileProvider cacheFileProvider;
    private RunningStateStorage runningStateStorage;
    private AchievementsStorage achievementsStorage;
    private RunRepository runRepository;
    private RunDb runDb;

    public ProductionDependencyContainer(final Context context) {
        dependency = new Dependency(context);
    }


    @Override
    public Context getApplicationContext() {
        return dependency.getApplicationContext();
    }

    @Override
    public SchedulerProvider getSchedulerProvider() {
        if (schedulerProvider == null) {
            schedulerProvider = dependency.provideSchedulerProvider();
        }
        return schedulerProvider;
    }

    @Override
    public CacheFileProvider getCacheFileProvider() {
        if (cacheFileProvider == null) {
            cacheFileProvider = dependency.provideCacheFileProvider();
        }
        return cacheFileProvider;
    }

    @Override
    public RunningStateStorage getRunningStateStorage() {
        if (runningStateStorage == null) {
            runningStateStorage = dependency.provideRunningStateStorage();
        }
        return runningStateStorage;
    }

    @Override
    public AchievementsStorage getAchievementsStorage() {
        if (achievementsStorage == null) {
            achievementsStorage = dependency.provideAchievementsStorage();
        }
        return achievementsStorage;
    }

    @Override
    public RunRepository getRunRepository() {
        if (runRepository == null) {
            runRepository = dependency.provideRunRepository(getRunDao(), getSchedulerProvider());
        }
        return runRepository;
    }

    private RunDao getRunDao() {
        if (runDb == null) {
            runDb = dependency.provideRunDb();
        }
        return runDb.RunDao();
    }

}
