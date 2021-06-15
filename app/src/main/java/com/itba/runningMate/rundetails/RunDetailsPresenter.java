package com.itba.runningMate.rundetails;

import android.graphics.Bitmap;
import android.net.Uri;

import com.itba.runningMate.domain.Route;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.achievementsstorage.AchievementsStorage;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.rundetails.model.RunMetricsDetail;
import com.itba.runningMate.utils.providers.files.CacheFileProvider;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class RunDetailsPresenter {

    private final WeakReference<RunDetailsView> view;
    private final RunRepository runRepository;
    private final SchedulerProvider schedulerProvider;
    private final CacheFileProvider cacheFileProvider;
    private final RunningStateStorage runStorage;
    private final AchievementsStorage achievementsStorage;
    private final long runId;

    private double distance;
    private RunMetricsDetail detail;

    private final CompositeDisposable disposables;

    public RunDetailsPresenter(final CacheFileProvider cacheFileProvider,
                               final RunRepository runRepository,
                               final SchedulerProvider schedulerProvider,
                               final RunningStateStorage storage,
                               AchievementsStorage achievementsStorage, final long runId,
                               final RunDetailsView view) {
        this.view = new WeakReference<>(view);
        this.cacheFileProvider = cacheFileProvider;
        this.runRepository = runRepository;
        this.schedulerProvider = schedulerProvider;
        this.runStorage = storage;
        this.achievementsStorage = achievementsStorage;
        this.runId = runId;
        this.disposables = new CompositeDisposable();
    }

    public void onViewAttached() {
        disposables.add(runRepository.getRunMetrics(runId)
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(this::onReceivedRunMetrics, this::onReceivedRunMetricsError));
    }

    private void onReceivedRunMetrics(Run run) {
        if (view.get() == null) {
            return;
        }
        distance = run.getDistance();
        detail = RunMetricsDetail.from(run);
        view.get().showRunMetrics(detail);
    }

    public void onMapAttached() {
        disposables.add(runRepository.getRun(runId)
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(this::onReceivedRun, this::onReceivedRunMetricsError));
    }

    private void onReceivedRunMetricsError(Throwable throwable) {
        Timber.d("Failed to retrieve run route from db for run-id: %l", runId);
        if (view.get() != null) {
            view.get().showRunNotAvailableError();
        }
    }

    private void onReceivedRun(Run run) {
        if (view.get() != null) {
            view.get().showRoute(Route.from(run.getRoute()));
        }
    }

    public void onViewDetached() {
        disposables.dispose();
    }

    public void onDeleteButtonClick() {
        achievementsStorage.decreaseTotalDistance(this.distance);
        achievementsStorage.persistState();

        disposables.add(runRepository.deleteRun(runId)
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(this::onRunDeleted, this::onRunDeleteError));
    }

    public void onRunTitleModified(String newTitle) {
        disposables.add(runRepository.updateTitle(runId, newTitle)
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.computation())
                .subscribe(this::onRunTitleUpdated, this::onRunTitleUpdateError));
    }

    public void onRunTitleUpdated() {
        Timber.i("Successfully updated title");
    }

    public void onRunTitleUpdateError(Throwable throwable) {
        Timber.d("Failed to update title");
        if (view.get() != null) {
            view.get().showUpdateTitleError();
        }
    }

    public void onShareButtonClick() {
        if (view.get() == null) {
            return;
        }
        Bitmap bitmap = view.get().getMetricsImage(detail);
        File image = cacheFileProvider.getFile("runningmate-run-metrics.png");
        Uri uri = null;
        try {
            FileOutputStream outputStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = cacheFileProvider.getUriForFile(image);
        } catch (Exception e) {
            view.get().showShareRunError();
        }
        view.get().shareImageIntent(uri);
    }

    private void onRunDeleted() {
        if (view.get() != null) {
            view.get().endActivity();
        }
    }

    private void onRunDeleteError(Throwable throwable) {
        Timber.d("Failed to delete run from db for run-id: %l", runId);
        if (view.get() != null) {
            view.get().showDeleteError();
        }
    }

}
