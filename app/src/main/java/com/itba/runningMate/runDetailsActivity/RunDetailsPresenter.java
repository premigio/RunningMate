package com.itba.runningMate.runDetailsActivity;

import com.itba.runningMate.domain.Sprint;
import com.itba.runningMate.repository.sprint.SprintRepository;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;

public class RunDetailsPresenter {

    private final WeakReference<RunDetailsView> view;
    private final SprintRepository repo;
    private final SchedulerProvider sp;
    private final long item_id;

    private Disposable disposable;

    public RunDetailsPresenter(RunDetailsView view, SprintRepository repo, SchedulerProvider sp, long item_id) {
        this.view = new WeakReference<>(view);
        this.repo = repo;
        this.sp = sp;
        this.item_id = item_id;
    }

    public void onViewAttached() {
        disposable = repo.getSprint(item_id)
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::receivedRunList,this::onRunListError);
    }

    private void receivedRunList(Sprint sprint) {
        if (view != null && view.get() != null) {
            view.get().bindRunDetails(sprint);
        }
    }

    private void onRunListError(Throwable throwable) {
        //TODO: mensajito
    }

    public void onViewDetached() {
        disposable.dispose();
    }

    public void deleteSprint() {
        disposable = Completable.fromAction(()->repo.deleteSprint(item_id))
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::finishSprint,this::onRunListError);
    }

    private void finishSprint() {
        if (view != null && view.get() != null) {
            view.get().endActivity();
        }
    }
}
