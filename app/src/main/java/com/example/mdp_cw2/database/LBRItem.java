/**
 * LBR (Location-based reminder) database object.
 */

package com.example.mdp_cw2.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lbr_table")
public class LBRItem {
    /**
     * Auto-incrementing index.
     */
    @PrimaryKey(autoGenerate = true)
    private int index = 0;

    /**
     * Title - e.g., "Get milk"
     */
    public String title;

    /**
     * Latitude of the centre of the circle
     */
    public double latitude;

    /**
     * Longitude of the centre of the circle
     */
    public double longitude;

    /**
     * Radius of the circle
     */
    public double radius;

    /**
     * Default constructor method
     * @param title Title of the LBR
     * @param latitude Latitude of the centre of the circle
     * @param longitude Longitude of the centre of the circle
     * @param radius Radius of the circle
     */
    public LBRItem(String title, double latitude, double longitude, double radius) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    /**
     * Setter method for the index
     * @param index New index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Getter method for the index
     * @return Index of this row
     */
    public int getIndex() {
        return index;
    }
}
