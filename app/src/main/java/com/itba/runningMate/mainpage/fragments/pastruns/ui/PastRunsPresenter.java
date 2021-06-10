package com.itba.runningMate.mainpage.fragments.pastruns.ui;

import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class PastRunsPresenter {

    private final WeakReference<PastRunsView> view;
    private final RunRepository runRepository;
    private final SchedulerProvider schedulerProvider;

    private Disposable disposable;


    public PastRunsPresenter(final SchedulerProvider schedulerProvider, final RunRepository runRepository, PastRunsView view) {
        this.view = new WeakReference<>(view);
        this.runRepository = runRepository;
        this.schedulerProvider = schedulerProvider;
    }

    public void onViewAttached() {
        disposable = runRepository.getRunLazy()
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(this::receivedRunList, this::onRunListError);

    }

    private void onRunListError(final Throwable err) {
        Timber.d("Failed to retrieve runs from db");
        if (view.get() != null) {
            view.get().showNoPastRunsMessage();
        }
    }

    public void onViewDetached() {
        disposable.dispose();
    }

    public void receivedRunList(List<Run> runs) {
        if (view.get() == null) {
            return;
        }
        if (runs == null || runs.isEmpty()) {
            view.get().showNoPastRunsMessage();
            view.get().updatePastRuns(new LinkedList<>());
        } else {
            view.get().hideNoPastRunsMessage();
            view.get().updatePastRuns(runs);
        }
    }

    public void refreshAction() {
        this.onViewAttached();
    }

    public void onRunClick(long id) {
        if (view.get() != null) {
            view.get().launchRunDetails(id);
        }
    }
}
