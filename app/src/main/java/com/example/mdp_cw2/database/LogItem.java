package com.example.mdp_cw2.database;

import android.location.Location;
import android.util.Log;
import android.util.Pair;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Arrays;

@Entity(tableName = "log_table")
public class LogItem {
    @PrimaryKey(autoGenerate = true)
    private int index = 0;

    // start timestamp
    public long startTime;

    // end timestamp
    public long endTime;

    // type
    public LogType logType;

    // distance in km
    public float distance;

    // annotation text
    public String annotationText = "";

    // annotation image (?)
    // no idea how to store this - possibly as a URI?
    public String annotationImage = "";

    // annotation likedState
    // unrated by default
    public LogLiked likedState = LogLiked.UNRATED;

    public double startLatitude;
    public double startLongitude;
    public double endLatitude;
    public double endLongitude;

    public LogItem(long startTime, LogType logType, long endTime, double startLatitude, double startLongitude, double endLatitude, double endLongitude, float distance) {
        this.startTime = startTime;
        this.logType = logType;
        this.endTime = endTime;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.distance = distance;
    }

    @Ignore
    public LogItem(long startTime, LogType logType) {
        this.startTime = startTime;
        this.logType = logType;
    }

    @Ignore
    public LogItem(long startTime, long endTime, LogType logType, Location startLocation, Location endLocation, float distance) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.logType = logType;

        this.startLatitude = startLocation.getLatitude();
        this.startLongitude = startLocation.getLongitude();

        this.endLatitude = endLocation.getLatitude();
        this.endLongitude = endLocation.getLongitude();

        this.distance = distance;
    }

    public void setStartLocation(Location location) {
        this.startLatitude = location.getLatitude();
        this.startLongitude = location.getLongitude();
    }

    public void setEndLocation(Location location) {
        this.endLatitude = location.getLatitude();
        this.endLongitude = location.getLongitude();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
