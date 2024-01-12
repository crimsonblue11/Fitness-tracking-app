/**
 * LocationListener subclass.
 */

package com.example.mdp_cw2.home;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

public class MovrLocationListener implements LocationListener {
    /**
     * Last known location of the user.
     */
    private Location lastLocation;

    /**
     * Total distance elapsed since beginning tracking.
     */
    private float totalDistance = 0;

    /**
     * Getter method for total distance.
     *
     * @return Value of totalDistance variable.
     * @see #totalDistance
     */
    public float getTotalDistance() {
        return totalDistance;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // if no location has been set yet, don't update distance
        if (lastLocation != null) {
            totalDistance += location.distanceTo(lastLocation) / 1000;
        }

        // set last known location
        lastLocation = location;
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
