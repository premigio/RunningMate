package com.itba.runningMate.repository.sprint;

import com.itba.runningMate.db.SprintDao;
import com.itba.runningMate.domain.Sprint;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SprintRepositoryImpl implements SprintRepository {

    private final SprintDao sprintDao;

    public SprintRepositoryImpl(final SprintDao sprintDao) {
        this.sprintDao = sprintDao;
    }

    @Override
    public Flowable<List<Sprint>> getRoutes() {
        return sprintDao.getRoutes().map(SprintMapper::toModel);
    }

    @Override
    public Single<Sprint> getRoute(int uid) {
        return sprintDao.getRoute(uid).map(SprintMapper::toModel);
    }

    @Override
    public void insertRoute(Sprint route) {
        sprintDao.insertRoute(SprintMapper.toEntity(route))
                .onErrorComplete()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    public void deleteRoute(Sprint route) {
        sprintDao.deleteRoute(SprintMapper.toEntity(route))
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
