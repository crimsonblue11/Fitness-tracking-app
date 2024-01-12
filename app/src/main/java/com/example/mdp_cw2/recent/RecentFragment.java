package com.example.mdp_cw2.recent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdp_cw2.MainActivity;
import com.example.mdp_cw2.R;
import com.example.mdp_cw2.database.LogDao;
import com.example.mdp_cw2.database.LogItem;
import com.example.mdp_cw2.database.LogRoomDatabase;
import com.example.mdp_cw2.home.AppFragment;

import java.util.List;

public class RecentFragment extends AppFragment {
    private LogDao logDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        MainActivity activity = (MainActivity) getActivity();

        if (activity == null) {
            Log.d("COMP3018", "Activity was null");
            return null;
        }

        logDao = LogRoomDatabase.getDatabase(activity).logDao();

        RecyclerView mainRecycler = view.findViewById(R.id.recent_list);
        mainRecycler.setLayoutManager(new LinearLayoutManager(activity));
        LogItemViewAdapter adapter = new LogItemViewAdapter(activity);

        activity.getViewModel().getAllLogs().observe(this.getViewLifecycleOwner(), new Observer<List<LogItem>>() {
            @Override
            public void onChanged(List<LogItem> logItems) {
                adapter.setData(logItems);
            }
        });

        mainRecycler.setAdapter(adapter);

        Button clearButton = view.findViewById(R.id.recent_clear);
        clearButton.setOnClickListener(l -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Clear recent logs")
                    .setMessage("This action will delete all recent activity logs. This cannot be undone.")
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {
                        LogRoomDatabase.databaseWriteExecutor.execute(() -> logDao.deleteAllLogs());
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                    .create().show();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
