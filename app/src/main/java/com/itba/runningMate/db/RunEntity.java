package com.itba.runningMate.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

@Entity(tableName = "runs")
public class RunEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "run_id")
    public long uid;

    @ColumnInfo(name = "route")
    public List<LatLng> route;

    @ColumnInfo(name = "start_time")
    public Date startTime;

    @ColumnInfo(name = "elapsed_time")
    public long elapsedTime;

    @ColumnInfo(name = "distance")
    public float distance;

    @ColumnInfo(name = "velocity")
    public float velocity;

    @ColumnInfo(name = "pace")
    public long pace;

    public RunEntity() {
    }

    public RunEntity uid(long uid) {
        this.uid = uid;
        return this;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public RunEntity route(List<LatLng> route) {
        this.route = route;
        return this;
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public void setRoute(List<LatLng> route) {
        this.route = route;
    }

    public RunEntity startTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public RunEntity elapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
        return this;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public RunEntity distance(float distance) {
        this.distance = distance;
        return this;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public RunEntity velocity(float velocity) {
        this.velocity = velocity;
        return this;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public RunEntity pace(long pace) {
        this.pace = pace;
        return this;
    }

    public long getPace() {
        return pace;
    }

    public void setPace(long pace) {
        this.pace = pace;
    }
}