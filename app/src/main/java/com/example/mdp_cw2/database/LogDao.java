package com.example.mdp_cw2.database;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(LogItem logItem);

    @Query("SELECT * FROM log_table " +
            "ORDER BY startTime DESC")
    LiveData<List<LogItem>> getAllLogs();

    @Query("DELETE FROM log_table")
    void deleteAll();

    @Query("UPDATE log_table " +
            "SET annotationText = :text, annotationImage = :image " +
            "WHERE `index`=:id")
    void updateAnnotation(int id, String text, String image);
}
