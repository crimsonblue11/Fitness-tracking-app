package com.example.mdp_cw2.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lbr_table")
public class LBRItem {
    @PrimaryKey(autoGenerate = true)
    private int index = 0;

    public String title;

    public double latitude;
    public double longitude;
    public float radius;

    public LBRItem(String title, double latitude, double longitude, float radius) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
