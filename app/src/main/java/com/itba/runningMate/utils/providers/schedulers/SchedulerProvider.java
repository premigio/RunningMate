package com.itba.runningMate.utils.providers.schedulers;

import io.reactivex.Scheduler;

public interface SchedulerProvider {

    Scheduler io();

    Scheduler computation();

    Scheduler ui();
}
