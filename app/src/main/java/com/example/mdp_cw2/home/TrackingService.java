/**
 * Service to track location and movement while running.
 */

package com.example.mdp_cw2.home;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.mdp_cw2.MainActivity;
import com.example.mdp_cw2.R;
import com.example.mdp_cw2.database.LogDao;
import com.example.mdp_cw2.database.LogItem;
import com.example.mdp_cw2.database.LogRoomDatabase;
import com.example.mdp_cw2.database.LogType;

public class TrackingService extends Service {
    /**
     * LocalBinder inner class
     */
    public class LocalBinder extends Binder {
        TrackingService getService() {
            return TrackingService.this;
        }
    }

    /**
     * LogType variable to keep track of the LogType, passed in via the starting Intent
     */
    private LogType logType;

    /**
     * Location object tracking the last known location. Once the service finishes, this will be
     * treated as the final location.
     */
    private Location currentLocation;

    /**
     * Location object storing the location the user was in when the service was started.
     */
    private Location initialLocation;

    /**
     * Instance of local binder class.
     */
    private final IBinder binder = new LocalBinder();

    /**
     * Notification ID of the foreground notification to be sent.
     */
    private static final int NOTIFICATION_ID = 1;

    /**
     * LocationManager instance. Manages user's location.
     */
    private LocationManager locationManager;

    /**
     * Instance of custom LocationListener
     *
     * @see MovrLocationListener
     */
    private MovrLocationListener locationListener;

    /**
     * Long keeping track of timestamp from when service was started.
     */
    private long startTime;

    /**
     * DAO object to access database.
     */
    private LogDao dao;

    public float getTotalDistance() {
        if (locationListener == null) {
            return -1f;
        }

        return locationListener.getTotalDistance();
    }

    /**
     * Method to get current location.
     * @return Location object containing last known location.
     */
    private Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean netEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            String locationProvider;
            if (!gpsEnabled && !netEnabled) {
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

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // can't request permissions from a service so just show an error
                Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
                return null;
            } else {
                // request location updates before requesting last known location
                locationManager.requestLocationUpdates(
                        locationProvider,
                        1000, 0,
                        locationListener
                );

                if (locationManager != null) {
                    // get last known location
                    currentLocation = locationManager.getLastKnownLocation(locationProvider);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentLocation;
    }

    @Override
    public void onDestroy() {
        // stop location updates
        locationManager.removeUpdates(locationListener);

        // create new log item and add it to SQLite database
        LogItem newLogItem = new LogItem(startTime, System.currentTimeMillis(), logType, initialLocation, currentLocation, locationListener.getTotalDistance());
        LogRoomDatabase.databaseWriteExecutor.execute(() -> dao.insertLog(newLogItem));

        Toast.makeText(this, "Added log!", Toast.LENGTH_SHORT).show();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // get DAO from database class
        dao = LogRoomDatabase.getDatabase(getApplicationContext()).logDao();

        // get log type from intent
        logType = LogType.valueOf(intent.getStringExtra("logType"));

        // pending intent to stop tracking service
        Intent stopIntent = new Intent(getApplicationContext(), TrackingService.class);
        stopIntent.setAction(StopTrackingReceiver.ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NOTIFICATION_ID, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        // pending intent to go to activity
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent activityIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);

        // create notification
        Notification notif = new NotificationCompat.Builder(this, MainActivity.TRACKING_CHANNEL_ID)
                .setContentTitle("Logging your movement")
                .setContentText("Movr is logging your activity.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(R.drawable.ic_launcher_foreground, "Stop", stopPendingIntent)
                .setContentIntent(activityIntent)
                .build();

        // start tracking notification as foreground so it can't be dismissed
        startForeground(NOTIFICATION_ID, notif);

        // start tracking
        initialLocation = getLocation();
        startTime = System.currentTimeMillis();

        return binder;
    }
}
