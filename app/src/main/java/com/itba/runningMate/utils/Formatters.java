package com.itba.runningMate.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Formatters {

    public static final SimpleDateFormat paceFormatter = new SimpleDateFormat("mm'' ss'\"'", Locale.getDefault());
    public static final DecimalFormat twoDecimalPlacesFormatter = new DecimalFormat("0.00");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
}
