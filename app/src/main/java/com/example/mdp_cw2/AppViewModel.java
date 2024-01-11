package com.example.mdp_cw2;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mdp_cw2.database.LBRItem;
import com.example.mdp_cw2.database.LogDao;
import com.example.mdp_cw2.database.LogItem;
import com.example.mdp_cw2.database.LogRoomDatabase;

import java.util.List;

public class AppViewModel extends AndroidViewModel {
    private LiveData<List<LogItem>> logs;
    private LiveData<List<LBRItem>> LBRs;

    public AppViewModel(Application application) {
        super(application);

        LogDao dao = LogRoomDatabase.getDatabase(application).logDao();
        LogRoomDatabase.databaseWriteExecutor.execute(() -> {
            logs = dao.getAllLogs();
            LBRs = dao.getAllLBRs();
        });
    }

    public LiveData<List<LogItem>> getAllLogs() {
        return logs;
    }

    public LiveData<List<LBRItem>> getLBRs() {
        return LBRs;
    }
}
