package com.itba.runningMate.rundetails.model;

import com.itba.runningMate.domain.Run;

import java.util.Date;

import static com.itba.runningMate.utils.Formatters.datetimeFormat;
import static com.itba.runningMate.utils.Formatters.hmsTimeFormatter;
import static com.itba.runningMate.utils.Formatters.paceFormatter;
import static com.itba.runningMate.utils.Formatters.timeFormat;
import static com.itba.runningMate.utils.Formatters.twoDecimalPlacesFormatter;

public class RunMetricsDetail {

    private String runTimeInterval;
    private String elapsedTime;
    private String runningTime;
    private String speed;
    private String pace;
    private String distance;
    private String title;
    private String calories;

    private RunMetricsDetail() {
    }

    public static RunMetricsDetail from(Run run) {
        RunMetricsDetail detail = new RunMetricsDetail();
        detail.speed = twoDecimalPlacesFormatter.format(run.getVelocity());
        detail.pace = paceFormatter.format(new Date(run.getPace()));
        detail.distance = twoDecimalPlacesFormatter.format(run.getDistance());
        detail.runTimeInterval = datetimeFormat.format(run.getStartTime()).concat(" - ").concat(timeFormat.format(run.getEndTime()));
        detail.elapsedTime = hmsTimeFormatter((run.getEndTime().getTime() - run.getStartTime().getTime()));
        detail.title = run.getTitle();
        detail.runningTime = hmsTimeFormatter(run.getRunningTime());
        detail.calories = Integer.toString(run.getCalories());
        return detail;
    }

    public String getRunTimeInterval() {
        return runTimeInterval;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public String getSpeed() {
        return speed;
    }

    public String getPace() {
        return pace;
    }

    public String getDistance() {
        return distance;
    }

    public String getTitle() {
        return title;
    }

    public String getCalories() {
        return calories;
    }
}
