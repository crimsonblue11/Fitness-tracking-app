/**
 * Database access object (DAO) class.
 * Used to access Room database
 *
 * @see com.example.mdp_cw2.database.LogRoomDatabase
 */

package com.example.mdp_cw2.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LogDao {
    /**
     * Method to insert log item into DB.
     * @param logItem Log object to insert.
     */
    @Insert(entity = LogItem.class, onConflict = OnConflictStrategy.IGNORE)
    void insertLog(LogItem logItem);

    /**
     * Method to insert LBR (location-based reminder) item into DB.
     * @param lbrItem LBR object to insert.
     */
    @Insert(entity = LBRItem.class, onConflict = OnConflictStrategy.IGNORE)
    void insertLBR(LBRItem lbrItem);

    /**
     * Method to get LiveData containing log table, sorted by start time.
     * @return LiveData object containing a list of all rows in the log table.
     */
    @Query("SELECT * FROM log_table " +
            "ORDER BY startTime DESC")
    LiveData<List<LogItem>> getAllLogs();

    /**
     * Method to get LiveData containing LBR table.
     * @return LiveData object containing a list of all rows in the LBR table.
     */
    @Query("SELECT * FROM lbr_table")
    LiveData<List<LBRItem>> getAllLBRs();

    /**
     * Method to drop all rows from log table.
     */
    @Query("DELETE FROM log_table")
    void deleteAllLogs();

    @Query("DELETE FROM lbr_table")
    void deleteAllLBRs();

    /**
     * Method to update the annotation data of a single log.
     * @param id ID of the row to change.
     * @param text Annotation text.
     */
    @Query("UPDATE log_table " +
            "SET annotationText = :text, likedState = :logLiked " +
            "WHERE `index`=:id")
    void updateAnnotation(int id, String text, LogLiked logLiked);

    /**
     * Method to get a single log by its row ID.
     * @param id ID of the log row to return.
     * @return Log item fetched from the database.
     */
    @Query("SELECT * FROM log_table " +
            "WHERE `index`=:id")
    LogItem getLog(int id);
}
