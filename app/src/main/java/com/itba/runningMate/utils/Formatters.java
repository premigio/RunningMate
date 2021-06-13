package com.itba.runningMate.utils;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Formatters {

    public static final SimpleDateFormat paceFormatter = new SimpleDateFormat("mm'' ss'\"'", Locale.getDefault());
    public static final DecimalFormat twoDecimalPlacesFormatter = new DecimalFormat("0.00");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
    public static final SimpleDateFormat datetimeFormat = new SimpleDateFormat("d/M/y HH:mm", Locale.getDefault());

    @SuppressLint("DefaultLocale")
    public static String hmsTimeFormatter(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        if (hours == 0) {
            return String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        } else {
            return String.format(
                    "%02d:%02d:%02d",
                    hours,
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        }
    }
}
