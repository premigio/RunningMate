package com.itba.runningMate.utils.providers.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class AndroidTestSchedulerProvider implements SchedulerProvider {

    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler computation() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler ui() {
        return Schedulers.trampoline();
    }
}
