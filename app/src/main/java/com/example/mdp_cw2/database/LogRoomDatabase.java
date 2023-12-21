package com.example.mdp_cw2.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {LogItem.class}, version = 1, exportSchema = false)
public abstract class LogRoomDatabase extends RoomDatabase {
    public abstract LogDao logDao();

    private static volatile LogRoomDatabase instance;

    private static final int threadCount = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(threadCount);

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
                dao.deleteAll();

                LogItem log1 = new LogItem(1701088069, 1701089809, LogType.WALK, 1.8f);
                LogItem log2 = new LogItem(1700036400, 1700040600, LogType.RUN, 2.78f);

                dao.insert(log1);
                dao.insert(log2);
            });
        }
    };
}
