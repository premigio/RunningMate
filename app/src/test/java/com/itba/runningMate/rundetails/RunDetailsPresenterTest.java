package com.itba.runningMate.rundetails;

import android.graphics.Bitmap;
import android.net.Uri;

import com.itba.runningMate.domain.Route;
import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.achievements.AchievementsRepository;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.rundetails.model.RunMetricsDetail;
import com.itba.runningMate.utils.providers.files.CacheFileProvider;
import com.itba.runningMate.utils.providers.schedulers.AndroidTestSchedulerProvider;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;

import io.reactivex.Completable;
import io.reactivex.Single;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RunDetailsPresenterTest {

    private final Run run = new Run.Builder().calories(10)
            .distance(10f)
            .pace(10L)
            .startTime(new Date(System.currentTimeMillis()))
            .endTime(new Date(System.currentTimeMillis()))
            .runningTime(10L)
            .velocity(10f)
            .uid(1L)
            .title("Test Run")
            .route(new LinkedList<>())
            .build();

    private CacheFileProvider cacheFileProvider;
    private RunRepository runRepository;
    private AchievementsRepository achievementsRepository;
    private SchedulerProvider schedulerProvider;
    private long runId;
    private RunDetailsView view;

    private RunDetailsPresenter presenter;
    private RunDetailsPresenter presenterSpy;

    @Before
    public void setUp() throws Exception {
        cacheFileProvider = mock(CacheFileProvider.class);
        runRepository = mock(RunRepository.class);
        schedulerProvider = new AndroidTestSchedulerProvider();
        achievementsRepository = mock(AchievementsRepository.class);
        view = mock(RunDetailsView.class);
        runId = 1;

        presenter = new RunDetailsPresenter(cacheFileProvider, runRepository, schedulerProvider, achievementsRepository, runId, view);
        presenterSpy = spy(presenter);
    }


    @Test
    public void givenViewAttachedThenQueryMetrics() {
        when(runRepository.getRunMetrics(runId)).thenReturn(Single.just(run));

        presenter.onViewAttached();

        verify(runRepository).getRunMetrics(runId);
    }

    @Test
    public void givenViewAttachedWhenQueryMetricsResultThenShowRunMetrics() {
        when(runRepository.getRunMetrics(runId)).thenReturn(Single.just(run));

        presenter.onViewAttached();

        verify(view).showRunMetrics(any(RunMetricsDetail.class));
    }

    @Test
    public void givenViewAttachedWhenQueryMetricsResultErrorThenShowErrorMessage() {
        when(runRepository.getRunMetrics(runId)).thenReturn(Single.error(new RuntimeException()));

        presenter.onViewAttached();

        verify(view).showRunNotAvailableError();
    }

    @Test
    public void givenViewDetachedThenDoNothing() {
        presenter.onViewDetached();
    }


    @Test
    public void givenMapAttachedThenQueryRunRoute() {
        when(runRepository.getRun(runId)).thenReturn(Single.just(run));

        presenter.onMapAttached();

        verify(runRepository).getRun(runId);
    }

    @Test
    public void givenMapAttachedWhenQueryRunRouteResultThenShowRoute() {
        when(runRepository.getRun(runId)).thenReturn(Single.just(run));

        ArgumentCaptor<Route> argumentCaptor = ArgumentCaptor.forClass(Route.class);

        presenter.onMapAttached();

        verify(view).showRoute(argumentCaptor.capture());
        Route capturedArgument = argumentCaptor.getValue();
        assert capturedArgument.getLocations().containsAll(run.getRoute());
    }

    @Test
    public void givenMapAttachedWhenQueryRunRouteResultErrorThenShowErrorMessage() {
        when(runRepository.getRun(runId)).thenReturn(Single.error(new RuntimeException()));

        presenter.onMapAttached();

        verify(view).showRunNotAvailableError();
    }

    @Test
    public void givenDeleteButtonClickWhenRunDeletedThenEndActivity() {
        when(runRepository.deleteRun(runId)).thenReturn(Completable.complete());

        presenter.onDeleteButtonClick();

        verify(view).endActivity();
    }

    @Test
    public void givenDeleteButtonClickWhenRunNotDeletedThenEndActivity() {
        when(runRepository.deleteRun(runId)).thenReturn(Completable.error(new RuntimeException()));

        presenter.onDeleteButtonClick();

        verify(view).showDeleteError();
    }

    @Test
    public void givenTitleModifiedThenSaveNewTitle() {
        String newTitle = "Mock title";
        when(runRepository.updateTitle(runId, newTitle)).thenReturn(Completable.complete());

        presenterSpy.onRunTitleModified(newTitle);

        verify(presenterSpy).onRunTitleUpdated();
    }

    @Test
    public void givenTitleModifiedWhenSaveErrorThenShowErrorMessage() {
        String newTitle = "Mock title";
        when(runRepository.updateTitle(runId, newTitle)).thenReturn(Completable.error(new RuntimeException()));

        presenter.onRunTitleModified(newTitle);

        verify(view).showUpdateTitleError();
    }

    @Test
    public void givenShareButtonClickedThenShareImage() {
        Bitmap bitmap = mock(Bitmap.class);
        Uri uri = mock(Uri.class);
        File file = mock(File.class);
        when(cacheFileProvider.getFile(any(String.class))).thenReturn(file);
        when(view.getMetricsImage(any(RunMetricsDetail.class))).thenReturn(bitmap);
        when(cacheFileProvider.getUriForFile(any(File.class))).thenReturn(uri);

        presenter.onShareButtonClick();

        verify(view).shareImageIntent(uri);
    }

}