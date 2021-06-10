package com.itba.runningMate.di;

import android.content.Context;

import com.itba.runningMate.mainpage.fragments.running.repository.RunningStateStorage;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.file.CacheFileProvider;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

public interface DependencyContainer {

    Context getApplicationContext();

    SchedulerProvider getSchedulerProvider();

    CacheFileProvider getCacheFileProvider();

    RunRepository getRunRepository();

    RunningStateStorage getRunningStateStorage();


}
