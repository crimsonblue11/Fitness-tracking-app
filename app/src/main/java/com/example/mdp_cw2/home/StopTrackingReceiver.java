package com.example.mdp_cw2.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopTrackingReceiver extends BroadcastReceiver {
    public static final String ACTION_STOP = "action_stop";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == null) {
            // TODO - error handling
            return;
        }

        if(intent.getAction().equals(ACTION_STOP)) {
            // TODO - somehow stop TrackingService from running
            Intent stopIntent = new Intent(context, TrackingService.class);
            context.stopService(stopIntent);
        }
    }
}
