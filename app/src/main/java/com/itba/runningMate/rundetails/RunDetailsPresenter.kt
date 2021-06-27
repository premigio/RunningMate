package com.itba.runningMate.rundetails

import com.itba.runningMate.domain.Route
import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.achievements.AchievementsStorage
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.rundetails.model.RunMetricsDetail
import com.itba.runningMate.rundetails.model.RunMetricsDetail.Companion.from
import com.itba.runningMate.utils.ImageProcessing
import com.itba.runningMate.utils.providers.files.CacheFileProvider
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.FileOutputStream
import java.lang.ref.WeakReference

class RunDetailsPresenter(
    private val cacheFileProvider: CacheFileProvider,
    private val runRepository: RunRepository,
    private val schedulerProvider: SchedulerProvider,
    private val achievementsStorage: AchievementsStorage,
    private val runId: Long,
    view: RunDetailsView?
) {
    private val view: WeakReference<RunDetailsView> = WeakReference(view)
    private val disposables: CompositeDisposable = CompositeDisposable()

    private var distance = 0.0
    private lateinit var detail: RunMetricsDetail

    fun onViewAttached() {
        disposables.add(runRepository.getRunMetrics(runId)
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ run: Run -> onReceivedRunMetrics(run) }) { throwable: Throwable ->
                onReceivedRunMetricsError(
                    throwable
                )
            })
    }

    private fun onReceivedRunMetrics(run: Run) {
        if (view.get() == null) {
            return
        }
        distance = run.distance.toDouble()
        detail = from(run)
        view.get()!!.showRunMetrics(detail)
    }

    fun onMapAttached() {
        disposables.add(runRepository.getRun(runId)
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ run: Run -> onReceivedRun(run) }) { throwable: Throwable ->
                onReceivedRunMetricsError(
                    throwable
                )
            })
    }

    private fun onReceivedRunMetricsError(throwable: Throwable) {
        Timber.d("Failed to retrieve run route from db for run-id: %l", runId)
        if (view.get() != null) {
            view.get()!!.showRunNotAvailableError()
        }
    }

    private fun onReceivedRun(run: Run) {
        if (view.get() != null) {
            view.get()!!.showRoute(Route.from(run.route))
        }
    }

    fun onViewDetached() {
        disposables.dispose()
    }

    fun onDeleteButtonClick() {
        achievementsStorage.decreaseTotalDistance(distance)
        achievementsStorage.persistState()
        disposables.add(runRepository.deleteRun(runId)
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.ui())
            .subscribe({ onRunDeleted() }) { throwable: Throwable -> onRunDeleteError(throwable) })
    }

    fun onRunTitleModified(newTitle: String?) {
        disposables.add(runRepository.updateTitle(runId, newTitle)
            .subscribeOn(schedulerProvider.computation())
            .observeOn(schedulerProvider.computation())
            .subscribe({ onRunTitleUpdated() }) { throwable: Throwable? ->
                onRunTitleUpdateError(
                    throwable
                )
            })
    }

    fun onRunTitleUpdated() {
        Timber.i("Successfully updated title")
    }

    private fun onRunTitleUpdateError(throwable: Throwable?) {
        Timber.d("Failed to update title")
        if (view.get() != null) {
            view.get()!!.showUpdateTitleError()
        }
    }

    fun onShareButtonClick() {
        if (view.get() == null) {
            return
        }
        val image = cacheFileProvider.getFile("runningmate-run-metrics.png")
        try {
            val outputStream = FileOutputStream(image)
            ImageProcessing.compress(view.get()!!.getMetricsImage(detail), outputStream)
        } catch (e: Exception) {
            view.get()!!.showShareRunError()
        }
        view.get()!!.shareImageIntent(cacheFileProvider.getUriForFile(image))
    }

    private fun onRunDeleted() {
        if (view.get() != null) {
            view.get()!!.endActivity()
        }
    }

    private fun onRunDeleteError(throwable: Throwable) {
        Timber.d("Failed to delete run from db for run-id: %l", runId)
        if (view.get() != null) {
            view.get()!!.showDeleteError()
        }
    }

}