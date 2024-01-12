package com.example.mdp_cw2;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.mdp_cw2.home.FragmentManagerActivity;
import com.example.mdp_cw2.home.HomeFragment;
import com.example.mdp_cw2.map.MapsFragment;
import com.example.mdp_cw2.recent.RecentFragment;
import com.example.mdp_cw2.stats.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends FragmentManagerActivity {
    public static final String TRACKING_CHANNEL_ID = "TrackingChannel";
    public static final String REMINDER_CHANNEL_ID = "LocationReminderChannel";
    private AppViewModel appViewModel;

    public ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(
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

    public AppViewModel getViewModel() {
        return appViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannelForReminders();
        createTrackingNotificationChannel();

        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        showFirstFragment(new HomeFragment(), R.id.main_frameLayout);

        BottomNavigationView bottomNav = findViewById(R.id.main_bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_nav_home) {
                // go to home / tracking fragment
                switchFragment(new HomeFragment(), R.id.main_frameLayout);
            } else if (id == R.id.bottom_nav_map) {
                // go to map fragment
                switchFragment(new MapsFragment(), R.id.main_frameLayout);
            } else if (id == R.id.bottom_nav_stats) {
                // go to navigation fragment
                switchFragment(new StatsFragment(), R.id.main_frameLayout);
            } else if (id == R.id.bottom_nav_recent) {
                // go to recent logs fragment
                switchFragment(new RecentFragment(), R.id.main_frameLayout);
            }
            return true;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        previousFragment();
        return true;
    }

    private void createNotificationChannelForReminders() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Location Reminder service";
            String desc = "Used for sending location-based reminder notifications";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(REMINDER_CHANNEL_ID, name, importance);
            channel.setDescription(desc);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void createTrackingNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Tracking service";
            String desc = "Used for tracking user to create a movement log";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(TRACKING_CHANNEL_ID, name, importance);
            channel.setDescription(desc);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public boolean checkLocationAccess() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationAccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location permissions")
                .setMessage("Movr needs access to your device's precise location to track and log your exercises. Without this permission, this feature will be disabled and will not work correctly.");
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            locationPermissionRequest.launch(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            });
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }
}
