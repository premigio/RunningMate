package com.itba.runningMate.repository.sprint;

import com.itba.runningMate.db.SprintDao;
import com.itba.runningMate.domain.Sprint;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class SprintRepositoryImpl implements SprintRepository {

    private final SprintDao sprintDao;
    private final SchedulerProvider scheduler;

    public SprintRepositoryImpl(final SprintDao sprintDao, final SchedulerProvider scheduler) {
        this.sprintDao = sprintDao;
        this.scheduler = scheduler;
    }

    @Override
    public Flowable<List<Sprint>> getSprint() {
        return sprintDao.getRoutes().map(SprintMapper::toModel);
    }

    @Override
    public Single<Sprint> getSprint(long uid) {
        return sprintDao.getRoute(uid).map(SprintMapper::toModel);
    }

    @Override
    public void insertSprint(Sprint sprint) {
        sprintDao.insertRoute(SprintMapper.toEntity(sprint))
                .onErrorComplete()
                .observeOn(scheduler.ui())
                .subscribeOn(scheduler.io())
                .subscribe();
    }

    @Override
    public void deleteSprint(Sprint sprint) {
        sprintDao.deleteRoute(SprintMapper.toEntity(sprint))
                .onErrorComplete()
                .subscribeOn(scheduler.io())
                .observeOn(scheduler.ui())
                .subscribe();
    }
}
