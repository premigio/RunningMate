package com.itba.runningMate.fragments.pastruns;

import com.itba.runningMate.fragments.history.model.DummyRView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RunListPresenter {

    private final WeakReference<RunListView> view;
    List<DummyRView> model; //todo desapaecer

    public RunListPresenter(RunListView view) {
        this.view = new WeakReference<>(view);
    }

    public void onViewAttached() {
        //List<DummyRView> model = new ArrayList<>(); //TODO cambair para que busque desde la db
        RunListView rlView = view.get();
        if (rlView != null) {
            prepareItems();
            rlView.updateOldRuns(model);
        }
    }

    public void onViewDetached() {

    }


    public void refreshAction(){
            model.add(new DummyRView("Item" + model.size(), "dummy with number " + model.size()));
    }


    private void prepareItems() { //todo: aunque esto se va al jocara
        model = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            DummyRView dummy = new DummyRView("Item" + i, "dummy with number " + i);
            model.add(dummy);
        }
    }


    public void onRunClick(DummyRView model) {
        if (view.get() != null) {
            view.get().showModelToast(model);
        }
    }

    public void onRunClick(int position) {
        if (view.get() != null) {
            view.get().showModelToast(model.get(position));
        }
    }
}
