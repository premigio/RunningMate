package com.itba.runningMate.rundetails;

import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class RunDetailsPresenter {

    private final WeakReference<RunDetailsView> view;
    private final RunRepository repo;
    private final SchedulerProvider sp;
    private final long itemId;

    private final CompositeDisposable disposables;

    public RunDetailsPresenter(RunDetailsView view, RunRepository repo, SchedulerProvider sp, long itemId) {
        this.view = new WeakReference<>(view);
        this.repo = repo;
        this.sp = sp;
        this.itemId = itemId;
        this.disposables = new CompositeDisposable();
    }

    public void onViewAttached() {
        disposables.add(repo.getRunMetrics(itemId)
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::receivedRunMetrics, this::onReceivedRunError));
    }

    private void receivedRunMetrics(Run run) {
        if (view.get() != null) {
            view.get().bindRunMetrics(run);
        }
    }

    public void onMapAttached() {
        disposables.add(repo.getRun(itemId)
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::onReceivedRun, this::onReceivedRunError));
    }

    private void onReceivedRunError(Throwable throwable) {
        Timber.d("Failed to retrieve run route from db for run-id: %l", itemId);
    }

    private void onReceivedRun(Run run) {
        if (view.get() != null) {
            view.get().bindRunRoute(run);
        }
    }

    public void onViewDetached() {
        disposables.dispose();
    }

    public void deleteRun() {
        disposables.add(Completable.fromAction(() -> repo.deleteRun(itemId))
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::endRunDetail, this::onEndRunError));
    }

    private void endRunDetail() {
        if (view.get() != null) {
            view.get().endActivity();
        }
    }

    private void onEndRunError(Throwable throwable) {
        Timber.d("Failed to delete run from db for run-id: %l", itemId);
    }
}
