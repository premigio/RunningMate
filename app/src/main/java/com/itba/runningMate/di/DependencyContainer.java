package com.itba.runningMate.di;

import android.content.Context;

import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.providers.files.CacheFileProvider;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

public interface DependencyContainer {

    Context getApplicationContext();

    SchedulerProvider getSchedulerProvider();

    CacheFileProvider getCacheFileProvider();

    RunRepository getRunRepository();

    RunningStateStorage getRunningStateStorage();


}
