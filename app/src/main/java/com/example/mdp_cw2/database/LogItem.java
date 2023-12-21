package com.example.mdp_cw2.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    public LogItem(long startTime, long endTime, LogType logType, float distance) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.logType = logType;
        this.distance = distance;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
