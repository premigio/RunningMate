package com.itba.runningMate.rundetails;

import android.graphics.Bitmap;
import android.net.Uri;

import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.file.CacheFileProvider;
import com.itba.runningMate.utils.schedulers.SchedulerProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class RunDetailsPresenter {

    private final WeakReference<RunDetailsView> view;
    private final RunRepository repo;
    private final SchedulerProvider sp;
    private final CacheFileProvider cacheFileProvider;
    private final long itemId;

    private final CompositeDisposable disposables;

    public RunDetailsPresenter(final CacheFileProvider cacheFileProvider,
                               final RunRepository repo,
                               final SchedulerProvider sp,
                               final long itemId,
                               final RunDetailsView view) {
        this.view = new WeakReference<>(view);
        this.cacheFileProvider = cacheFileProvider;
        this.repo = repo;
        this.sp = sp;
        this.itemId = itemId;
        this.disposables = new CompositeDisposable();
    }

    public void onViewAttached() {
        disposables.add(repo.getRunMetrics(itemId)
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::onReceivedRunMetrics, this::onReceivedRunMetricsError));
    }

    private void onReceivedRunMetrics(Run run) {
        if (view.get() != null) {
            view.get().bindRunMetrics(run);
        }
    }

    public void onMapAttached() {
        disposables.add(repo.getRun(itemId)
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::onReceivedRun, this::onReceivedRunMetricsError));
    }

    private void onReceivedRunMetricsError(Throwable throwable) {
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

    public void onDeleteButtonClick() {
        disposables.add(Completable.fromAction(() -> repo.deleteRun(itemId))
                .subscribeOn(sp.computation())
                .observeOn(sp.ui())
                .subscribe(this::onRunDeleted, this::onRunDeleteError));
    }

    public void onShareButtonClick() {
        if (view.get() == null) {
            return;
        }
        Bitmap bitmap = view.get().getMetricsImage();
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
        view.get().shareImageIntent(uri, bitmap);
    }

    private void onRunDeleted() {
        if (view.get() != null) {
            view.get().endActivity();
        }
    }

    private void onRunDeleteError(Throwable throwable) {
        Timber.d("Failed to delete run from db for run-id: %l", itemId);
    }

}
