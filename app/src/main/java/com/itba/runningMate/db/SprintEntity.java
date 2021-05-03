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
    public long uid;

    @ColumnInfo(name = "route")
    public List<LatLng> route;

    @ColumnInfo(name = "start_time")
    public Date startTime;

    @ColumnInfo(name = "elapsed_time")
    public long elapsedTime;

    @ColumnInfo(name = "distance")
    private float distance;

    @ColumnInfo(name = "velocity")
    private float velocity;

    @ColumnInfo(name = "pace")
    private long pace;

    public SprintEntity() {
    }

    public SprintEntity uid(long uid) {
        this.uid = uid;
        return this;
    }

    public long getUid() {
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

    public SprintEntity elapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
        return this;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public SprintEntity distance(float distance) {
        this.distance = distance;
        return this;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public SprintEntity velocity(float velocity) {
        this.velocity = velocity;
        return this;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public SprintEntity pace(long pace) {
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
