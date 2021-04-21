package com.itba.runningMate.domain;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class Sprint {

    private int uid;
    private List<LatLng> route;
    private Date startTime;
    private Date endTime;

    public Sprint() {
    }

    public Sprint uid(int uid) {
        this.uid = uid;
        return this;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Sprint endTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Sprint{" +
                "uid=" + uid +
                ", route=" + route +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
