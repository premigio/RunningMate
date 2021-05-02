package com.itba.runningMate.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class Sprint {

    private int uid;
    private List<LatLng> route;
    private Date startTime;
    /* km */
    private float distance;
    /* km/h */
    private float velocity;
    private long pace;
    private long elapsedTime;

    public Sprint() {
    }

    public Sprint uid(int uid) {
        this.uid = uid;
        return this;
    }

    public int getUid() {
        return uid;
    }

    public Sprint route(List<LatLng> route) {
        this.route = route;
        return this;
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public void setRoute(List<LatLng> route) {
        this.route = route;
    }

    public Sprint startTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Sprint distance(float distance) {
        this.distance = distance;
        return this;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Sprint velocity(float velocity) {
        this.velocity = velocity;
        return this;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public Sprint pace(long pace) {
        this.pace = pace;
        return this;
    }

    public long getPace() {
        return pace;
    }

    public void setPace(long pace) {
        this.pace = pace;
    }

    public Sprint elapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
        return this;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
