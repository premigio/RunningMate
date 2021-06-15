package com.itba.runningMate.rundetails;

import com.itba.runningMate.domain.Run;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.providers.files.CacheFileProvider;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

import org.junit.Before;

import java.util.Date;
import java.util.LinkedList;

import static org.mockito.Mockito.mock;

public class RunDetailsPresenterTest {

    private final Run run = new Run().calories(10)
            .distance(10)
            .pace(10)
            .startTime(new Date(System.currentTimeMillis()))
            .endTime(new Date(System.currentTimeMillis()))
            .runningTime(10)
            .velocity(10)
            .uid(1)
            .title("Test Run")
            .route(new LinkedList<>());
    private CacheFileProvider cacheFileProvider;
    private RunRepository runRepository;
    private SchedulerProvider schedulerProvider;
    private long runId;
    private RunDetailsView view;
    private RunDetailsPresenter presenter;

    @Before
    public void setUp() throws Exception {
        cacheFileProvider = mock(CacheFileProvider.class);
        runRepository = mock(RunRepository.class);
        schedulerProvider = mock(SchedulerProvider.class);
        view = mock(RunDetailsView.class);
        runId = 1;

        presenter = new RunDetailsPresenter(cacheFileProvider,
                runRepository, schedulerProvider, runId, view);
    }

}