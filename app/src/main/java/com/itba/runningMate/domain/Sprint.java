package com.itba.runningMate.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class Sprint {

    private long uid;
    private List<LatLng> route;
    private Date startTime;
    private Date endTime;
    /* km */
    private float distance;
    /* km/h */
    private float velocity;
    private long pace;
    private long time;

    public Sprint() {
    }

    public Sprint uid(long uid) {
        this.uid = uid;
        return this;
    }

    public long getUid() {
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

    public Sprint endTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public long getElapsedMillis() {
        return endTime.getTime() - startTime.getTime();
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

    public Sprint time(long time) {
        this.time = time;
        return this;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
