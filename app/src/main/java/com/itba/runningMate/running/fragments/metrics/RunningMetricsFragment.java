package com.itba.runningMate.running.fragments.metrics;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itba.runningMate.R;
import com.itba.runningMate.di.DependencyContainer;
import com.itba.runningMate.di.DependencyContainerLocator;
import com.itba.runningMate.repository.runningstate.RunningStateStorage;
import com.itba.runningMate.services.location.Tracker;
import com.itba.runningMate.services.location.TrackingService;
import com.itba.runningMate.repository.run.RunRepository;
import com.itba.runningMate.utils.providers.schedulers.SchedulerProvider;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.itba.runningMate.utils.Formatters.hmsTimeFormatter;

public class RunningMetricsFragment extends Fragment implements RunningMetricsView, ServiceConnection {

    // todo: save presenter and saveinstance fragment
    private static final SimpleDateFormat paceFormatter = new SimpleDateFormat("mm'' ss'\"'", Locale.getDefault());
    private static final DecimalFormat twoDecimalPlacesFormatter = new DecimalFormat("0.00");

    private FloatingActionButton pauseButton;
    private FloatingActionButton playButton;
    private FloatingActionButton stopButton;
    private TextView stopWatch;
    private TextView distance;
    private TextView calories;
    private TextView pace;

    private RunningMetricsPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_running_metrics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createPresenter();

        pauseButton = view.findViewById(R.id.pause);
        playButton = view.findViewById(R.id.play);
        stopButton = view.findViewById(R.id.stop);
        distance = view.findViewById(R.id.distance);
        pace = view.findViewById(R.id.pace);
        calories = view.findViewById(R.id.calories);
        stopWatch = view.findViewById(R.id.running_time);

        setUpButtons();
    }

    private void setUpButtons() {
        stopButton.setOnLongClickListener(l -> {
            presenter.onStopButtonClick();
            return true;
        });
        enlargeOnTouch(stopButton);
        pauseButton.setOnClickListener(l -> presenter.onPauseButtonClick());
        playButton.setOnClickListener(l -> presenter.onPlayButtonClick());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void enlargeOnTouch(FloatingActionButton btn) {
        btn.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = (float) 1.25;
                float y = (float) 1.25;
                btn.setScaleX(x);
                btn.setScaleY(y);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                float x = 1;
                float y = 1;
                btn.setScaleX(x);
                btn.setScaleY(y);
            }
            return false;
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        presenter.onViewAttached();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        presenter.onViewDetached();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void attachTrackingService() {
        Intent intent = new Intent(getActivity(), TrackingService.class);
        this.getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void detachTrackingService() {
        this.getActivity().unbindService(this);
        presenter.onTrackingServiceDetached();
    }

    public void createPresenter() {
        final DependencyContainer container = DependencyContainerLocator.locateComponent(this.getActivity());
        final RunningStateStorage stateStorage = container.getRunningStateStorage();
        final SchedulerProvider schedulerProvider = container.getSchedulerProvider();
        final RunRepository runRepository = container.getRunRepository();

        presenter = new RunningMetricsPresenter(stateStorage, runRepository, schedulerProvider, this);
    }

    @Override
    public void updateDistance(float elapsedDistance) {
        distance.setText(twoDecimalPlacesFormatter.format(elapsedDistance));
    }

    @Override
    public void updateCalories(int calories) {
        this.calories.setText(String.valueOf(calories));
    }

    @Override
    public void updateStopwatch(long elapsedTime) {
        stopWatch.setText(hmsTimeFormatter(elapsedTime));
    }

    @Override
    public void updatePace(long pace) {
        this.pace.setText(paceFormatter.format(new Date(pace)));
    }

    @Override
    public void showInitialMetrics() {
        pace.setText(R.string.text_view_running_initial_pace);
        distance.setText(R.string.text_view_running_initial_distance);
        stopWatch.setText(R.string.text_view_running_initial_time);
        calories.setText(R.string.text_view_running_initial_calories);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        final Tracker tracker = (Tracker) service;
        presenter.onTrackingServiceAttached(tracker);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        presenter.onTrackingServiceDetached();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void showSaveRunError() {
        Toast.makeText(this.getActivity(), getText(R.string.toast_error_run_save), Toast.LENGTH_LONG).show();
    }

    @Override
    public void launchRunActivity(long runId) {
        Uri uri = new Uri.Builder().scheme("runningmate")
                .authority("run")
                .appendQueryParameter("run-id", String.valueOf(runId)).build();
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
        this.getActivity().finish();
    }

    @Override
    public void finishActivity() {
        this.getActivity().finish();
    }

    @Override
    public void showStopConfirm() {
        AlertDialog.Builder alertBox = new AlertDialog.Builder(this.getActivity());
        alertBox.setMessage(R.string.stop_run_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> presenter.stopRun())
                .setNegativeButton(R.string.no, (dialog, which) -> {
                })
                .show();
    }

    @Override
    public void showStopBtn() {
        stopButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPlayBtn() {
        playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPauseBtn() {
        pauseButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideStopBtn() {
        stopButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hidePlayBtn() {
        playButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hidePauseBtn() {
        pauseButton.setVisibility(View.INVISIBLE);
    }
}
