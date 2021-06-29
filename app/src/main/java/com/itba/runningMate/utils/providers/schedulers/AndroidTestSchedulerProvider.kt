package com.itba.runningMate.utils.providers.schedulers

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class AndroidTestSchedulerProvider : SchedulerProvider {

    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun computation(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun ui(): Scheduler {
        return Schedulers.trampoline()
    }
}