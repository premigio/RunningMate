package com.itba.runningMate.mainpage.fragments.pastruns.ui;

import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class PastRunsPresenter {

    private final WeakReference<PastRunsView> view;
    private final RunRepository runRepository;
    private final SchedulerProvider sp;

    private Disposable disposable;


    public PastRunsPresenter(final SchedulerProvider sp, final RunRepository runRepository, PastRunsView view) {
        this.view = new WeakReference<>(view);
        this.runRepository = runRepository;
        this.sp = sp;
    }

    public void onViewAttached() {
        disposable = runRepository.getRunLazy()
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::receivedRunList, this::onRunListError);

    }

    private void onRunListError(final Throwable err) {
        //TODO mostrar un mensaje tipo toast que hubo un error
    }

    public void onViewDetached() {
        disposable.dispose();
    }

    public void receivedRunList(List<Run> runs) {
        if (view.get() != null) {
            view.get().updateOldRuns(runs);
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
