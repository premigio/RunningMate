package com.itba.runningMate.fragments.pastruns;

import com.itba.runningMate.domain.Sprint;
import com.itba.runningMate.repository.sprint.SprintRepository;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class RunListPresenter {

    private final WeakReference<RunListView> view;
    private final SprintRepository repo;
    private final SchedulerProvider sp;

    private Disposable disposable;
    //List<DummyRView> model; //todo desapaecer

    public RunListPresenter(final SchedulerProvider sp, final SprintRepository repo, RunListView view) {
        this.view = new WeakReference<>(view);
        this.repo = repo;
        this.sp = sp;
    }

    public void onViewAttached() {
        disposable = repo.getSprint()
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::receivedRunList,this::onRunListError);

    }

    private void onRunListError(final Throwable err) {
        //TODO mostrar un mensaje tipo toast que hubo un error
    }

    public void onViewDetached() {
        disposable.dispose();
    }

    public void receivedRunList(List<Sprint> sprints){
        RunListView rlView = view.get();
        if (rlView != null) {
            rlView.updateOldRuns(sprints);
        }
    }

    public void refreshAction(){
            this.onViewAttached();
    }

    public void onRunClick(int position) {

        if (view.get() != null) {
            view.get().showModelToast(position);
        }
    }

    private void workWithClick(int position) {

    }




}
