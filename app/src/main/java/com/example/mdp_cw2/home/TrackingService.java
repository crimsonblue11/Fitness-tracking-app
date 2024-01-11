package com.example.mdp_cw2.home;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.mdp_cw2.MainActivity;
import com.example.mdp_cw2.R;
import com.example.mdp_cw2.database.LogDao;
import com.example.mdp_cw2.database.LogItem;
import com.example.mdp_cw2.database.LogRoomDatabase;
import com.example.mdp_cw2.database.LogType;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class TrackingService extends Service {
    public class LocalBinder extends Binder {
        TrackingService getService() {
            return TrackingService.this;
        }
    }

    FusedLocationProviderClient locationProviderClient;

    boolean canGetLocation = false;
    Location currentLocation;
    Location initialLocation;
    double currentLat;
    double currentLong;
    private final IBinder binder = new LocalBinder();
    private static final String CHANNEL_ID = "TrackingChannel";
    private static final int NOTIFICATION_ID = 1;
    LocationManager locationManager;
    MovrLocationListener locationListener;
    long startTime;
    LogDao dao;

    public double getLongitude() {
        return currentLong;
    }

    public double getLatitude() {
        return currentLat;
    }

    public boolean canGetLocation() {
        return canGetLocation;
    }

    public float getTotalDistance() {
        if (locationListener == null) {
            return -1f;
        }

        return locationListener.getTotalDistance();
    }

    private Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean netEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            String locationProvider;
            if (!gpsEnabled && !netEnabled) {
                canGetLocation = false;
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                return null;
            } else if (netEnabled) {
                // prefer network provider to GPS provider as it is faster
                Log.d("COMP3018", "Using network provider");
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                // if network provider is unavailable, use GPS
                Log.d("COMP3018", "Using GPS provider");
                locationProvider = LocationManager.GPS_PROVIDER;
            }

            locationListener = new MovrLocationListener();

            canGetLocation = true;
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // get perm
                return null;
            } else {
                locationManager.requestLocationUpdates(
                        locationProvider,
                        1000, 0,
                        locationListener
                );

                if (locationManager != null) {
                    currentLocation = locationManager.getLastKnownLocation(locationProvider);
                    if (currentLocation != null) {
                        currentLat = currentLocation.getLatitude();
                        currentLong = currentLocation.getLongitude();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentLocation;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
//        initialLocation = getLocation();

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);

        LogItem newLogItem = new LogItem(startTime, System.currentTimeMillis(), LogType.WALK, initialLocation, currentLocation, locationListener.getTotalDistance());

        dao = LogRoomDatabase.getDatabase(getApplicationContext()).logDao();
        LogRoomDatabase.databaseWriteExecutor.execute(() -> dao.insert(newLogItem));

        Toast.makeText(this, "Added log!", Toast.LENGTH_SHORT).show();

//        stopForeground(NOTIFICATION_ID);

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        dao = LogRoomDatabase.getDatabase(getApplicationContext()).logDao();

        // pending intent to stop tracking service
        Intent stopIntent = new Intent(getApplicationContext(), TrackingService.class);
        stopIntent.setAction(StopTrackingReceiver.ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NOTIFICATION_ID, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        // pending intent to go to activity
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent activityIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notif = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Logging your movement")
                .setContentText("Movr is logging your activity.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(R.drawable.ic_launcher_foreground, "Stop", stopPendingIntent)
                .setContentIntent(activityIntent)
                .build();

        startForeground(NOTIFICATION_ID, notif);

        // start tracking
        initialLocation = getLocation();
        Log.d("COMP3018", initialLocation.getLatitude() + ", " + initialLocation.getLongitude());

        startTime = System.currentTimeMillis();

        return binder;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Tracking service";
            String desc = "Used for tracking user to create a movement log";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(desc);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
