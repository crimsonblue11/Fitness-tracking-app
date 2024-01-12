/**
 * ViewModel class. Allows persistent data between activity config changes.
 */

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
    /**
     * LiveData object storing a list of all log database objects.
     */
    private LiveData<List<LogItem>> logs;

    /**
     * LiveData object storing a list of all LBR database objects.
     */
    private LiveData<List<LBRItem>> LBRs;

    /**
     * Constructor method. Instantiates all LiveData objects.
     *
     * @param application Application reference
     */
    public AppViewModel(Application application) {
        super(application);

        LogDao dao = LogRoomDatabase.getDatabase(application).logDao();
        LogRoomDatabase.databaseWriteExecutor.execute(() -> {
            logs = dao.getAllLogs();
            LBRs = dao.getAllLBRs();
        });
    }

    /**
     * Method to get logs LiveData object
     *
     * @return LiveData object containing a list of all logs
     */
    public LiveData<List<LogItem>> getAllLogs() {
        return logs;
    }

    /**
     * Method to get LBR LiveData object
     *
     * @return LiveData object containing a list of all LBRs
     */
    public LiveData<List<LBRItem>> getLBRs() {
        return LBRs;
    }
}
