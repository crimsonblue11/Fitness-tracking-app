package com.example.mdp_cw2.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {LogItem.class, LBRItem.class}, version = 4, exportSchema = false)
public abstract class LogRoomDatabase extends RoomDatabase {
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
                
//                LogItem log1 = new LogItem(1701088069, 1701089809, LogType.WALK, new Location(), new Pair<>(0.0, 0.0));
//                LogItem log2 = new LogItem(1700036400, 1700040600, LogType.RUN, new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));

//                dao.insert(log1);
//                dao.insert(log2);

                LBRItem lbr1 = new LBRItem("Test", 53.074330, -2.519859, 100f);
                dao.insert(lbr1);
            });
        }
    };
}
