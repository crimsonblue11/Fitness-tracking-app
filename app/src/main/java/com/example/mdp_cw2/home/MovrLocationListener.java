package com.example.mdp_cw2.home;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

public class MovrLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("COMP3018", location.getLatitude() + " " + location.getLatitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("COMP3018", "onStatusChanged: " + provider + " " + status);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("COMP3018", "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("COMP3018", "onProviderDisabled: " + provider);
    }
}
