package com.example.mdp_cw2.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LogDao {
    @Insert(entity = LogItem.class, onConflict = OnConflictStrategy.IGNORE)
    void insert(LogItem logItem);

    @Insert(entity = LBRItem.class, onConflict = OnConflictStrategy.IGNORE)
    void insert(LBRItem lbrItem);

    @Query("SELECT * FROM log_table " +
            "ORDER BY startTime DESC")
    LiveData<List<LogItem>> getAllLogs();

    @Query("SELECT * FROM lbr_table")
    List<LBRItem> getAllLBRs();

    @Query("DELETE FROM log_table")
    void deleteAllLogs();

    @Query("UPDATE log_table " +
            "SET annotationText = :text, annotationImage = :image " +
            "WHERE `index`=:id")
    void updateAnnotation(int id, String text, String image);

    @Query("SELECT * FROM log_table " +
            "WHERE `index`=:index")
    LogItem getLog(int index);
}
