package com.example.mdp_cw2.home;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.mdp_cw2.R;

public class TrackingService extends Service {
    private static final String CHANNEL_ID = "TrackingChannel";
    private static final int NOTIFICATION_ID = 1;

    MovrLocationListener locationListener;
    LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("COMP3018", "Started");

        String ACTION_STOP = "stop";

        Intent stopIntent = new Intent(getApplicationContext(), TrackingService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notif = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Logging your movement")
                .setContentText("Movr is logging your activity.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(R.drawable.ic_launcher_foreground, "Stop", stopPendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notif);

        // TODO - start tracking
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MovrLocationListener();

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5, locationListener);
        } catch(SecurityException e) {
            Log.d("COMP3018", e.toString());
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("COMP3018", "Stopped");
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
