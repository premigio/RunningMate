package com.itba.runningMate.di;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.itba.runningMate.db.RunDao;
import com.itba.runningMate.db.RunDb;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.repository.runningstate.RunningStateStorageImpl;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.repository.run.RunRepositoryImpl;
import com.itba.runningMate.utils.providers.files.CacheFileProvider;
import com.itba.runningMate.utils.providers.files.CacheFileProviderImpl;
import com.itba.runningMate.utils.providers.schedulers.AndroidSchedulerProvider;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

public class Dependency {

    private final Context context;

    public Dependency(Context context) {
        this.context = context.getApplicationContext();
    }

    public Context getApplicationContext() {
        return context;
    }

    public SchedulerProvider provideSchedulerProvider() {
        return new AndroidSchedulerProvider();
    }

    public CacheFileProvider provideCacheFileProvider() {
        return new CacheFileProviderImpl(getApplicationContext());
    }

    public RunningStateStorage provideRunningStateStorage() {
        final SharedPreferences preferences = getApplicationContext()
                .getSharedPreferences(RunningStateStorage.LANDING_STATE_PREFERENCES_FILE, Context.MODE_PRIVATE);
        return new RunningStateStorageImpl(preferences);
    }

    public RunRepository provideRunRepository(RunDao runDao, SchedulerProvider schedulerProvider) {
        return new RunRepositoryImpl(runDao, schedulerProvider);
    }

    public RunDb provideRunDb() {
        return Room.databaseBuilder(getApplicationContext(), RunDb.class, RunDb.NAME)
                .fallbackToDestructiveMigration()
                .build();
    }
}
