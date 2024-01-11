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
    private TrackingService trackingService;
    private boolean isBound = false;
    private final Handler handler = new Handler();
    private TextView timeView;
    private TextView distanceView;
    private LogType logType = LogType.WALK;
    private Button startButton;
    private Button stopButton;
    private MainActivity activity;
    private int secondsElapsed = 0;
    ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean courseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                // TODO - something with this
                if (fineLocationGranted != null && fineLocationGranted) {
                    // precise location access granted
                    Log.d("COMP3018", "Fine permission granted");
                } else if (courseLocationGranted != null && courseLocationGranted) {
                    // only approx location granted
                    Log.d("COMP3018", "Approx permission granted");
                } else {
                    // no location granted
                    Log.d("COMP3018", "No permission granted");
                }
            }
    );

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
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Location permissions")
                        .setMessage("Movr needs access to your device's precise location to track and log your exercises. Without this permission, this feature will be disabled and will not work correctly.");
                builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                    locationPermissionRequest.launch(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                    });
                });
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
                builder.create().show();
            } else {
                // TODO - figure out the log ID of the current log
//                newLogItem = new LogItem(System.currentTimeMillis(), logType);

                Intent intent = new Intent(activity, TrackingService.class);
//                intent.putExtra("LogID", newLogItem.getIndex());
                activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
                homeType.setVisibility(View.INVISIBLE);
                homeTypeButtons.setVisibility(View.INVISIBLE);
            }
        });

        stopButton = view.findViewById(R.id.home_stop);
        stopButton.setOnClickListener(l -> {
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
        @Override
        public void run() {
            if (isBound) {
                // run every sec
                handler.postDelayed(this, 1000);

                // update timer and UI thread
                secondsElapsed += 1;
                timeView.setText(Util.secondsToTimeStamp(secondsElapsed));

                distanceView.setText(String.format(Locale.ENGLISH, "%.2f km", trackingService.getTotalDistance()));
            }
        }
    };
}
