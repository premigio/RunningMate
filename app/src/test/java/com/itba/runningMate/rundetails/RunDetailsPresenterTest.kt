package com.itba.runningMate.rundetails

import android.graphics.Bitmap
import android.net.Uri
import com.itba.runningMate.domain.Route
import com.itba.runningMate.domain.Run
import com.itba.runningMate.repository.achievements.AchievementsRepository
import com.itba.runningMate.repository.run.RunRepository
import com.itba.runningMate.utils.providers.files.CacheFileProvider
import com.itba.runningMate.utils.providers.schedulers.AndroidTestSchedulerProvider
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import java.io.File
import java.util.*

class RunDetailsPresenterTest {

    private val run = Run.Builder().calories(10)
        .distance(10f)
        .pace(10L)
        .startTime(Date(System.currentTimeMillis()))
        .endTime(Date(System.currentTimeMillis()))
        .runningTime(10L)
        .velocity(10f)
        .uid(1L)
        .title("Test Run")
        .route(LinkedList())
        .build()

    private lateinit var cacheFileProvider: CacheFileProvider
    private lateinit var runRepository: RunRepository
    private lateinit var achievementsRepository: AchievementsRepository
    private lateinit var schedulerProvider: SchedulerProvider
    private var runId: Long = 0
    private lateinit var view: RunDetailsView
    private lateinit var presenter: RunDetailsPresenter
    private lateinit var presenterSpy: RunDetailsPresenter

    @Before
    @Throws(Exception::class)
    fun setUp() {
        cacheFileProvider = Mockito.mock(CacheFileProvider::class.java)
        runRepository = Mockito.mock(RunRepository::class.java)
        schedulerProvider = AndroidTestSchedulerProvider()
        achievementsRepository = Mockito.mock(AchievementsRepository::class.java)
        view = Mockito.mock(RunDetailsView::class.java)
        runId = 1
        presenter = RunDetailsPresenter(
            cacheFileProvider,
            runRepository,
            schedulerProvider,
            runId,
            view
        )
        presenterSpy = Mockito.spy(presenter)
    }

    @Test
    fun givenViewAttachedThenQueryMetrics() {
        Mockito.`when`(runRepository.getRunMetrics(runId)).thenReturn(Single.just(run))
        presenter.onViewAttached()
        Mockito.verify(runRepository).getRunMetrics(runId)
    }

    @Test
    fun givenViewAttachedWhenQueryMetricsResultThenShowRunMetrics() {
        Mockito.`when`(runRepository.getRunMetrics(runId)).thenReturn(Single.just(run))
        presenter.onViewAttached()
        Mockito.verify(view).showRunMetrics(any())
    }

    @Test
    fun givenViewAttachedWhenQueryMetricsResultErrorThenShowErrorMessage() {
        Mockito.`when`(runRepository.getRunMetrics(runId)).thenReturn(
            Single.error(
                RuntimeException()
            )
        )
        presenter.onViewAttached()
        Mockito.verify(view).showRunNotAvailableError()
    }

    @Test
    fun givenViewDetachedThenDoNothing() {
        presenter.onViewDetached()
    }

    @Test
    fun givenMapAttachedThenQueryRunRoute() {
        Mockito.`when`(runRepository.getRun(runId)).thenReturn(Single.just(run))
        presenter.onMapAttached()
        Mockito.verify(runRepository).getRun(runId)
    }

    @Test
    fun givenMapAttachedWhenQueryRunRouteResultThenShowRoute() {
        Mockito.`when`(runRepository.getRun(runId)).thenReturn(Single.just(run))

        val argumentCaptor = argumentCaptor<Route>()
        presenter.onMapAttached()
        Mockito.verify(view).showRoute(argumentCaptor.capture())
//        assert(argumentCaptor.allValues.containsAll(run.route))
    }

    @Test
    fun givenMapAttachedWhenQueryRunRouteResultErrorThenShowErrorMessage() {
        Mockito.`when`(runRepository.getRun(runId)).thenReturn(Single.error(RuntimeException()))
        presenter.onMapAttached()
        Mockito.verify(view).showRunNotAvailableError()
    }

    @Test
    fun givenDeleteButtonClickWhenRunDeletedThenEndActivity() {
        Mockito.`when`(runRepository.deleteRun(runId)).thenReturn(Completable.complete())
        presenter.onDeleteButtonClick()
        Mockito.verify(view).endActivity()
    }

    @Test
    fun givenDeleteButtonClickWhenRunNotDeletedThenEndActivity() {
        Mockito.`when`(runRepository.deleteRun(runId)).thenReturn(
            Completable.error(
                RuntimeException()
            )
        )
        presenter.onDeleteButtonClick()
        Mockito.verify(view).showDeleteError()
    }

    @Test
    fun givenTitleModifiedThenSaveNewTitle() {
        val newTitle = "Mock title"
        Mockito.`when`(runRepository.updateTitle(runId, newTitle))
            .thenReturn(Completable.complete())
        presenterSpy.onRunTitleModified(newTitle)
        Mockito.verify(presenterSpy).onRunTitleUpdated()
    }

    @Test
    fun givenTitleModifiedWhenSaveErrorThenShowErrorMessage() {
        val newTitle = "Mock title"
        Mockito.`when`(runRepository.updateTitle(runId, newTitle)).thenReturn(
            Completable.error(
                RuntimeException()
            )
        )
        presenter.onRunTitleModified(newTitle)
        Mockito.verify(view).showUpdateTitleError()
    }

    @Test
    fun givenShareButtonClickedThenShareImage() {
        val bitmap = Mockito.mock(Bitmap::class.java)
        val uri = Mockito.mock(Uri::class.java)
        val file = Mockito.mock(File::class.java)
        Mockito.`when`(
            cacheFileProvider.getFile(
                any()
            )
        ).thenReturn(file)
        Mockito.`when`(
            view.getMetricsImage(
                any()
            )
        ).thenReturn(bitmap)
        Mockito.`when`(
            cacheFileProvider.getUriForFile(
                any()
            )
        ).thenReturn(uri)
        presenter.onShareButtonClick()
        Mockito.verify(view).shareImageIntent(uri)
    }
}