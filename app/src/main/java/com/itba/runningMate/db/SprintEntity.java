package com.itba.runningMate.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

@Entity(tableName = "sprints")
public class SprintEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sprint_id")
    public int uid;

    @ColumnInfo(name = "route")
    public List<LatLng> route;

    @ColumnInfo(name = "start_time")
    public Date startTime;

    @ColumnInfo(name = "end_time")
    public Date endTime;

    /*@ColumnInfo(name = "kms")
    public double kms;*/

    public SprintEntity() {
    }

    public SprintEntity uid(int uid) {
        this.uid = uid;
        return this;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public SprintEntity route(List<LatLng> route) {
        this.route = route;
        return this;
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public void setRoute(List<LatLng> route) {
        this.route = route;
    }

    public SprintEntity startTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public SprintEntity endTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

}
