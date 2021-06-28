package com.itba.runningMate.mainpage.fragments.feed;

import com.itba.runningMate.R;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class FeedPresenter {

    private final WeakReference<FeedView> view;
    private final RunRepository repo;
    private final SchedulerProvider schedulerProvider;

    private final CompositeDisposable disposables; // I need 2 disposables at least


    public FeedPresenter(final RunRepository repo,
                         final SchedulerProvider schedulerProvider,
                         final FeedView view) {
        this.view = new WeakReference<>(view);
        this.repo = repo;
        this.schedulerProvider = schedulerProvider;
        disposables = new CompositeDisposable();
    }

    public void onViewAttached() {
        disposables.add(repo.getRunLazy()
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(this::receivedRunList, this::onRunListError));
        getGoalLevel();
    }

    public void onViewDetached() {
        disposables.clear();
    }

    private void onRunListError(Throwable throwable) {
        Timber.d("Failed to retrieve runs from db");
        if (view.get() != null) {
            view.get().setPastRunCardsNoText();
        }
    }

    private void receivedRunList(List<Run> runs) {
        Timber.i("Runs %d", runs.size());
        if (view.get() != null) {
            if (runs.isEmpty()) {
                view.get().setPastRunCardsNoText();
                view.get().disappearRuns(0);
                return;
            }
            view.get().disappearNoText();
            int maxVal = Math.min(runs.size(), 3);
            for (int i = 1; i <= maxVal; i++) {
                //add data to view
                view.get().addRunToCard(i - 1, runs.get(i - 1));
            }
            // disappear the run cards where they should not be
            view.get().disappearRuns(maxVal);
        }
    }

    public void onPastRunClick(long id) {
        if (view.get() != null) {
            view.get().launchRunDetailActivity(id);
        }
    }

    public void goToPastRunsActivity() {
        if (view.get() != null) {
            view.get().launchPastRunsActivity();
        }
    }

    private void getGoalLevel() {
        disposables.add(repo.getTotalDistance()
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(this::receivedTotalDistance, this::onRunListErrorGoals));
    }

    private void receivedTotalDistance(double distance) {
        if (view.get() != null) {
            if (distance < 100.0) { // Taragui
                view.get().setGoalTitle(R.string.taragui);
                view.get().setGoalSubtitle(R.string.taragui_subtitle);
                view.get().setGoalImage(R.drawable.taragui);
            } else if (distance < 200.0) { // CBSé
                view.get().setGoalTitle(R.string.cbse);
                view.get().setGoalSubtitle(R.string.cbse_subtitle);
                view.get().setGoalImage(R.drawable.cbse);
            } else if (distance < 300.0) { // Cruz de Malta
                view.get().setGoalTitle(R.string.cruz_de_malta);
                view.get().setGoalSubtitle(R.string.cruz_de_malta_subtitle);
                view.get().setGoalImage(R.drawable.cruzdemalta);
            } else if (distance < 500.0) { // Playadito
                view.get().setGoalTitle(R.string.playadito);
                view.get().setGoalSubtitle(R.string.playadito_subtitle);
                view.get().setGoalImage(R.drawable.playadito);
            } else if (distance < 750.0) { // Rosamonte
                view.get().setGoalTitle(R.string.rosamonte);
                view.get().setGoalSubtitle(R.string.rosamonte_subtitle);
                view.get().setGoalImage(R.drawable.rosamonte);
            } else { // La Merced
                view.get().setGoalTitle(R.string.merced);
                view.get().setGoalSubtitle(R.string.merced_subtitle);
                view.get().setGoalImage(R.drawable.lamerced);
            }
        }
    }

    private void onRunListErrorGoals(Throwable throwable) {
        Timber.d("Failed to retrieve total distance from db");
        if (view.get() != null) {
            view.get().setGoalTitle(R.string.taragui);
            view.get().setGoalSubtitle(R.string.taragui_subtitle);
            view.get().setGoalImage(R.drawable.taragui);
        }
    }

    public void goToAchievementsActivity() {
        if (view.get() != null) {
            view.get().launchAchievementsActivity();
        }
    }
}
