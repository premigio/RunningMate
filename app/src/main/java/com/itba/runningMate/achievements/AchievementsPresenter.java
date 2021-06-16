package com.itba.runningMate.achievements;

import com.itba.runningMate.R;
import com.itba.runningMate.achievements.achievement.Achievements;
import com.itba.runningMate.repository.achievements.AchievementsStorage;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class AchievementsPresenter {

    private final WeakReference<AchievementsView> view;
    private final CompositeDisposable disposables;
    private final RunRepository repo;
    private final SchedulerProvider schedulerProvider;
    private final AchievementsStorage storage;


    public AchievementsPresenter(final RunRepository repo,
                                 final SchedulerProvider schedulerProvider,
                                 final AchievementsStorage storage,
                                 final AchievementsView view) {
        this.view = new WeakReference<>(view);
        this.disposables = new CompositeDisposable();
        this.repo = repo;
        this.schedulerProvider = schedulerProvider;
        this.storage = storage;
    }

    public void onViewAttached() {
        receivedTotalDistance(storage.getTotalDistance());
        getAchievements();
    }

    private void getAchievements() {
        disposables.add(repo.getMaxSpeed()
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(this::receivedMaxSpeed, this::onRunListErrorGoals));

        disposables.add(repo.getMaxKcal()
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(this::receivedMaxKcal, this::onRunListErrorGoals));

        disposables.add(repo.getMaxTime()
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(this::receivedMaxTime, this::onRunListErrorGoals));

    }

    private void receivedMaxSpeed(double speed) {
        view.get().setAchievement(Achievements.SPEED10, speed >= 10.0);
    }

    private void receivedMaxKcal(double kcal) {
        view.get().setAchievement(Achievements.KCAL1000, kcal >= 1000.0);
    }

    private void receivedMaxTime(long time) {
        view.get().setAchievement(Achievements.TIME1H, time >= 3600000);
    }

    public void onViewDetached() {
        disposables.dispose();
    }

    private void receivedTotalDistance(double distance) {
        if (view.get() != null) {
            double progress = 0.0;
            if (distance < 100.0) { // Taragui
                view.get().setGoalTitle(R.string.taragui);
                view.get().setGoalSubtitle(R.string.taragui_subtitle);
                view.get().setGoalImage(R.drawable.taragui);
                view.get().setProgressBar(distance, 100.0);
            } else if (distance < 200.0) { // CBSé
                view.get().setGoalTitle(R.string.cbse);
                view.get().setGoalSubtitle(R.string.cbse_subtitle);
                view.get().setGoalImage(R.drawable.cbse);
                view.get().setProgressBar((distance - 100.0), 100);
            } else if (distance < 300.0) { // Cruz de Malta
                view.get().setGoalTitle(R.string.cruz_de_malta);
                view.get().setGoalSubtitle(R.string.cruz_de_malta_subtitle);
                view.get().setGoalImage(R.drawable.cruzdemalta);
                view.get().setProgressBar((distance - 200.0), 100.0);
            } else if (distance < 500.0) { // Playadito
                view.get().setGoalTitle(R.string.playadito);
                view.get().setGoalSubtitle(R.string.playadito_subtitle);
                view.get().setGoalImage(R.drawable.playadito);
                view.get().setProgressBar((distance - 300.0), 200.0);
            } else if (distance < 750.0) { // Rosamonte
                view.get().setGoalTitle(R.string.rosamonte);
                view.get().setGoalSubtitle(R.string.rosamonte_subtitle);
                view.get().setGoalImage(R.drawable.rosamonte);
                view.get().setProgressBar((distance - 500.0), 250.0);
            } else { // La Merced
                view.get().setGoalTitle(R.string.merced);
                view.get().setGoalSubtitle(R.string.merced_subtitle);
                view.get().setGoalImage(R.drawable.lamerced);
                view.get().setProgressBar(100.0, 100.0);
            }
            //achievement 1 unlocked
            view.get().setAchievement(Achievements.DISTANCE2000, distance >= 2000.0);

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

}
