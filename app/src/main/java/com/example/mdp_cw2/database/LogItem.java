/**
 * Log item database object
 */

package com.example.mdp_cw2.database;

import android.location.Location;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "log_table")
public class LogItem {
    /**
     * Auto-incrementing index of the row.
     */
    @PrimaryKey(autoGenerate = true)
    private int index = 0;

    /**
     * Millisecond timestamp at the start of the log
     */
    public long startTime;

    /**
     * Millisecond timestamp at the end of the log
     */
    public long endTime;

    /**
     * Log type
     * @see LogType
     */
    public LogType logType;

    /**
     * Distance of the activity, measure in Kilometres.
     */
    public float distance;

    /**
     * Text value of the associated annotation.
     */
    public String annotationText = "";

    /**
     * Liked state of the annotation.
     *
     * @see LogLiked
     */
    public LogLiked likedState = LogLiked.UNRATED;

    /**
     * Initial latitude of the activity.
     */
    public double startLatitude;

    /**
     * Initial longitude of the activity.
     */
    public double startLongitude;

    /**
     * Final known latitude of the activity.
     */
    public double endLatitude;

    /**
     * Final known longitude of the activity.
     */
    public double endLongitude;

    /**
     * Constructor method
     * @param startTime Start time in ms.
     * @param logType LogType value.
     * @param endTime End time in ms.
     * @param startLatitude Initial latitude.
     * @param startLongitude Initial longitude.
     * @param endLatitude Final latitude.
     * @param endLongitude Final longitude.
     * @param distance Distance in KM.
     */
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

    /**
     * Alternate constructor using Location objects rather than directly passing in coordinates.
     * @param startTime Start time in ms.
     * @param endTime End time in ms.
     * @param logType LogType value.
     * @param startLocation Location object storing the initial location.
     * @param endLocation Location object storing the final location.
     * @param distance Distance in KM.
     */
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

    /**
     * Getter method for index
     * @return Index of this row in the DB
     */
    public int getIndex() {
        return index;
    }

    /**
     * Setter method for the index
     * @param index New index
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
