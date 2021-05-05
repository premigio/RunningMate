package com.itba.runningMate.rundetails;

import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;

public class RunDetailsPresenter {

    private final WeakReference<RunDetailsView> view;
    private final RunRepository repo;
    private final SchedulerProvider sp;
    private final long item_id;

    private Disposable disposable;

    public RunDetailsPresenter(RunDetailsView view, RunRepository repo, SchedulerProvider sp, long item_id) {
        this.view = new WeakReference<>(view);
        this.repo = repo;
        this.sp = sp;
        this.item_id = item_id;
    }

    public void onViewAttached() {
        disposable = repo.getRun(item_id)
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::receivedRunList, this::onRunListError);
    }

    private void receivedRunList(Run run) {
        if (view.get() != null) {
            view.get().bindRunDetails(run);
        }
    }

    private void onRunListError(Throwable throwable) {
        //TODO: mensajito
    }

    public void onViewDetached() {
        disposable.dispose();
    }

    public void deleteRun() {
        disposable = Completable.fromAction(() -> repo.deleteRun(item_id))
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::endRunDetail, this::onRunListError);
    }

    private void endRunDetail() {
        if (view.get() != null) {
            view.get().endActivity();
        }
    }
}
