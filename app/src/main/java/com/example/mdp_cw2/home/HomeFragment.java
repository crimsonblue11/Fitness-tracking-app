/**
 * Home screen fragment.
 * Handles tracking activities.
 */

package com.example.mdp_cw2.home;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mdp_cw2.MainActivity;
import com.example.mdp_cw2.R;
import com.example.mdp_cw2.Util;
import com.example.mdp_cw2.database.LogItem;
import com.example.mdp_cw2.database.LogType;

import java.util.Locale;

public class HomeFragment extends AppFragment {
    /**
     * Tracking service instance.
     */
    private TrackingService trackingService;

    /**
     * Boolean tracking the status of the bound trackingService.
     */
    private boolean isBound = false;

    /**
     * Handler object to handle bound service looping.
     */
    private final Handler handler = new Handler();

    /**
     * View to track elapsed time in current activity.
     */
    private TextView timeView;

    /**
     * View to tracking total distance in current activity.
     */
    private TextView distanceView;

    /**
     * LogType of the current activity.
     * Set to LogType.WALK by default.
     */
    private LogType logType = LogType.WALK;

    /**
     * Button to start activity tracking service.
     */
    private Button startButton;

    /**
     * Button to stop activity tracking service.
     */
    private Button stopButton;

    /**
     * Reference to parent activity.
     */
    private MainActivity activity;

    /**
     * Number of seconds elapsed in the current activity.
     */
    private int secondsElapsed = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        if (activity == null) {
            Log.d("COMP3018", "Activity is null");
            return;
        }

        TextView homeType = view.findViewById(R.id.home_type);
        LinearLayout homeTypeButtons = view.findViewById(R.id.home_type_layout);

        timeView = view.findViewById(R.id.home_time);
        distanceView = view.findViewById(R.id.home_distance);

        startButton = view.findViewById(R.id.home_start);
        startButton.setOnClickListener(v -> {
            // check location first
            if (!activity.checkLocationAccess()) {
                // if location access isn't granted, try requesting it
                activity.requestLocationAccess();
            } else {
                // else location access must be granted
                // start tracking service and bind it to the activity
                Intent intent = new Intent(activity, TrackingService.class);
                intent.putExtra("logType", logType.toString());
                activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
                homeType.setVisibility(View.INVISIBLE);
                homeTypeButtons.setVisibility(View.INVISIBLE);
            }
        });

        stopButton = view.findViewById(R.id.home_stop);
        stopButton.setOnClickListener(l -> {
            // stop tracking service by unbinding from activity
            activity.unbindService(serviceConnection);
            isBound = false;
            secondsElapsed = 0;

            timeView.setText(R.string.time_placeholder);

            stopButton.setVisibility(View.GONE);
            startButton.setVisibility(View.VISIBLE);
            homeType.setVisibility(View.VISIBLE);
            homeTypeButtons.setVisibility(View.VISIBLE);
        });

        ImageButton walkButton = view.findViewById(R.id.home_type_walk);
        walkButton.setOnClickListener(l -> {
            logType = LogType.WALK;
            homeType.setText(String.format("%s%s", getString(R.string.activity_type), getString(R.string.walk)));
        });

        ImageButton runButton = view.findViewById(R.id.home_type_run);
        runButton.setOnClickListener(l -> {
            logType = LogType.RUN;
            homeType.setText(String.format("%s%s", getString(R.string.activity_type), getString(R.string.run)));
        });

        ImageButton cycleButton = view.findViewById(R.id.home_type_cycle);
        cycleButton.setOnClickListener(l -> {
            logType = LogType.CYCLE;
            homeType.setText(String.format("%s%s", getString(R.string.activity_type), getString(R.string.cycle)));
        });
    }

    @Override
    public void onDestroy() {
        if (isBound) {
            activity.unbindService(serviceConnection);
            isBound = false;
        }
        super.onDestroy();
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TrackingService.LocalBinder binder = (TrackingService.LocalBinder) iBinder;
            trackingService = binder.getService();
            isBound = true;
            handler.post(updateRunnable);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    private final Runnable updateRunnable = new Runnable() {
        /**
         * Static constant representing the delay between runs of the handler.
         * Set to 1000 ms (1 second)
         */
        private final static int DELAY_MS = 1000;

        @Override
        public void run() {
            if (isBound) {
                // run every DELAY_MS
                handler.postDelayed(this, DELAY_MS);

                // update timer
                secondsElapsed += 1;
                timeView.setText(Util.secondsToTimeStamp(secondsElapsed));

                // update distance
                distanceView.setText(String.format(Locale.ENGLISH, "%.2f km", trackingService.getTotalDistance()));
            }
        }
    };
}
