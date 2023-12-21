package com.example.mdp_cw2.recent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdp_cw2.R;
import com.example.mdp_cw2.database.LogDao;
import com.example.mdp_cw2.database.LogItem;
import com.example.mdp_cw2.database.LogRoomDatabase;
import com.example.mdp_cw2.database.LogType;
import com.example.mdp_cw2.home.AppFragment;
import com.example.mdp_cw2.home.FragmentManagerActivity;
import com.example.mdp_cw2.home.LogItemViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecentFragment extends AppFragment {
    LogRoomDatabase db;
    LogDao logDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        FragmentManagerActivity activity = (FragmentManagerActivity) getActivity();

        if(activity == null) {
            Log.d("COMP3018", "Activity was null");
            return null;
        }

        db = LogRoomDatabase.getDatabase(activity);
        logDao = db.logDao();

        List<LogItem> sampleList = new ArrayList<>();
        sampleList.add(new LogItem(1701088069, 1701089809, LogType.WALK, 1.8f));
        sampleList.add(new LogItem(1700036400, 1700040600, LogType.RUN, 2.78f));

        RecyclerView mainRecycler = view.findViewById(R.id.recent_list);
        mainRecycler.setLayoutManager(new LinearLayoutManager(activity));
        LogItemViewAdapter adapter = new LogItemViewAdapter(activity, sampleList);

        logDao.getAllLogs().observe(this.getViewLifecycleOwner(), new Observer<List<LogItem>>() {
            @Override
            public void onChanged(List<LogItem> logItems) {
                adapter.setData(logItems);
            }
        });

        mainRecycler.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
