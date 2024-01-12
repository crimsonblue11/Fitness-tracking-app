/**
 * Broadcast receiver to check for Geofence status changes.
 * Used to send notifications for LBRs.
 */

package com.example.mdp_cw2.map;

import android.Manifest;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mdp_cw2.MainActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    /**
     * Static constant representing ID of the notification that will be sent.
     */
    private static final int NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        if (event == null || event.hasError()) {
            Log.d("COMP3018", "Geofencing error");
            return;
        }

        // get LBR title from the intent
        String reminderText = intent.getStringExtra("lbrName");

        // get the specific type of transition from the event
        int transition = event.getGeofenceTransition();

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // if entering geofence, show a notification
            Notification notif = new NotificationCompat.Builder(context, MainActivity.REMINDER_CHANNEL_ID)
                    .setContentTitle("Reminder: " + reminderText)
                    .setContentText("This is a location-based reminder")
                    .build();

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // don't do anything
                return;
            }
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notif);

        } else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // remove notification
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
        }
    }


}
