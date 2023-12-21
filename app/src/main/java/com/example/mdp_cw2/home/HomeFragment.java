package com.example.mdp_cw2.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.mdp_cw2.R;

public class HomeFragment extends AppFragment {
    Button startButton;
    Button stopButton;
    Button addAnnotationButton;

    ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean courseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

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

        FragmentManagerActivity activity = (FragmentManagerActivity) getActivity();

        if(activity == null) {
            Log.d("COMP3018", "Activity is null");
            return;
        }

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
                Intent intent = new Intent(activity, TrackingService.class);
                activity.startService(intent);

                // TODO - figure out the log ID of the current log

                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
                addAnnotationButton.setVisibility(View.VISIBLE);
            }
        });

        stopButton = view.findViewById(R.id.home_stop);
        stopButton.setOnClickListener(l -> {
            Intent intent = new Intent(activity, TrackingService.class);
            activity.stopService(intent);
            stopButton.setVisibility(View.GONE);
            startButton.setVisibility(View.VISIBLE);
            addAnnotationButton.setVisibility(View.GONE);
        });

        addAnnotationButton = view.findViewById(R.id.home_add_annotation);
        addAnnotationButton.setOnClickListener(l -> {
            Intent intent = new Intent(activity, AddAnnotationActivity.class);
            startActivity(intent);
        });
    }
}
