/**
 * ROOM Database class.
 */

package com.example.mdp_cw2.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {LogItem.class, LBRItem.class}, version = 5, exportSchema = false)
public abstract class LogRoomDatabase extends RoomDatabase {
    /*
    Most of the code in this file is from Lab 06 - Room Database Storage
     */

    public abstract LogDao logDao();
    private static volatile LogRoomDatabase instance;
    private static final int threadCount = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(threadCount);

    public static LogRoomDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (LogRoomDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), LogRoomDatabase.class, "log_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(createCallback)
                            .build();
                }
            }
        }
        return instance;
    }

    private static final RoomDatabase.Callback createCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                LogDao dao = instance.logDao();
                dao.deleteAllLogs();

                LBRItem lbr1 = new LBRItem("Test", 53.074330, -2.519859, 100f);
                dao.insertLBR(lbr1);
            });
        }
    };
}
