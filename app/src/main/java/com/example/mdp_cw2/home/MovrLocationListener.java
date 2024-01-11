package com.example.mdp_cw2.home;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

public class MovrLocationListener implements LocationListener {
    private Location lastLocation;
    private float totalDistance = 0;

    public float getTotalDistance() {
        return totalDistance;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("COMP3018", location.getLatitude() + ", " + location.getLongitude());

        if (lastLocation != null) {
            // edge case for initial location, where last location won't be set yet
            totalDistance += location.distanceTo(lastLocation) / 1000;
        }
        lastLocation = location;
//        Log.d("COMP3018", String.valueOf(totalDistance));
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
