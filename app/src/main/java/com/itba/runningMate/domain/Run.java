package com.itba.runningMate.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class Run {

    private long uid;
    private List<LatLng> route;
    private Date startTime;
    /* km */
    private float distance;
    /* km/h */
    private float velocity;
    private long pace;
    private long elapsedTime;
    private int calories;

    public Run() {
    }

    public Run uid(long uid) {
        this.uid = uid;
        return this;
    }

    public long getUid() {
        return uid;
    }

    public Run route(List<LatLng> route) {
        this.route = route;
        return this;
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public void setRoute(List<LatLng> route) {
        this.route = route;
    }

    public Run startTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Run distance(float distance) {
        this.distance = distance;
        return this;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Run velocity(float velocity) {
        this.velocity = velocity;
        return this;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public Run pace(long pace) {
        this.pace = pace;
        return this;
    }

    public long getPace() {
        return pace;
    }

    public void setPace(long pace) {
        this.pace = pace;
    }

    public Run elapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
        return this;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public Run calories(int calories) {
        this.calories = calories;
        return this;
    }
}
