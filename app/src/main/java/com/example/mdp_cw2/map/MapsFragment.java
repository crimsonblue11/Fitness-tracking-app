/**
 * Fragment containing Google Map component.
 * Also used for managing and viewing LBRs.
 */

package com.example.mdp_cw2.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.room.RoomDatabase;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mdp_cw2.MainActivity;
import com.example.mdp_cw2.R;
import com.example.mdp_cw2.database.LBRItem;
import com.example.mdp_cw2.database.LogDao;
import com.example.mdp_cw2.database.LogRoomDatabase;
import com.example.mdp_cw2.home.AppFragment;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IMapFragmentDelegate;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MapsFragment extends AppFragment {
    /**
     * Reference to parent activity.
     */
    private MainActivity activity;

    /**
     * Geofencing client instance.
     */
    private GeofencingClient geofencingClient;

    /**
     * DAO object.
     */
    private LogDao dao;

    /**
     * Button to add an LBR after adding a circle to the map.
     */
    private Button addLBRButton;

    /**
     * Button to cancel adding an LBR after adding a circle to the map.
     */
    private Button cancelButton;

    /**
     * Edit text for the title of the reminder to add.
     */
    private EditText reminderEditText;

    /**
     * List of all LBRs to display on the map.
     */
    final ArrayList<LBRItem> LBRs = new ArrayList<>();

    /**
     * Reference to the current LBR that is being added, if adding an LBR (null if otherwise).
     */
    private Circle newLBR;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            activity.getViewModel().getLBRs().observe(activity, lbrItems -> {
                LBRs.clear();
                LBRs.addAll(lbrItems);

                for (LBRItem lbr : LBRs) {
                    // convert all LBRs into circles on the map
                    googleMap.addCircle(new CircleOptions()
                            .center(new LatLng(lbr.latitude, lbr.longitude))
                            .radius(lbr.radius)
                            .clickable(true)
                            .fillColor(R.color.black)
                            .visible(true)
                            .clickable(true)
                    );
                }

                googleMap.setOnMapClickListener(l -> {
                    // upon clicking the map, a new circle is added
                    // if this is submitted it will become a new LBR and Geofence

                    if (newLBR != null) {
                        // remove previous circle
                        // would be null if the previous circle was submitted as an LBR
                        newLBR.remove();
                    }

                    newLBR = googleMap.addCircle(new CircleOptions()
                            .center(new LatLng(l.latitude, l.longitude))
                            .radius(100)
                            .clickable(true)
                            .fillColor(R.color.black));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(l.latitude, l.longitude)));

                    addLBRButton.setEnabled(true);
                    reminderEditText.setEnabled(true);
                    cancelButton.setEnabled(true);
                });

                googleMap.setOnCircleClickListener(l -> {
                    Object circleTag = l.getTag();
                    String circleTitle;

                    if (circleTag == null) {
                        circleTitle = "Null";
                    } else {
                        circleTitle = circleTag.toString();
                    }

                    new AlertDialog.Builder(activity)
                            .setTitle(circleTitle)
                            .setPositiveButton("Ok", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            }).create().show();
                });
            });

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestLocationAccess();
                return;
            }
            googleMap.setMyLocationEnabled(true);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        geofencingClient = LocationServices.getGeofencingClient(activity);
        dao = LogRoomDatabase.getDatabase(getContext()).logDao();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        addLBRButton = view.findViewById(R.id.map_add_reminder);
        addLBRButton.setOnClickListener(l -> {
            // convert circle added to map to LBR database object and geofence
            String lbrTitle = reminderEditText.getText().toString();
            reminderEditText.getText().clear();

            // add to db
            LBRItem newLBRItem = new LBRItem(lbrTitle, newLBR.getCenter().latitude, newLBR.getCenter().longitude, newLBR.getRadius());
            LogRoomDatabase.databaseWriteExecutor.execute(() -> dao.insertLBR(newLBRItem));

            newLBR.setTag(newLBRItem.title);

            // create geofence
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setCircularRegion(newLBRItem.latitude, newLBRItem.longitude, 10f)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                    .addGeofence(geofence)
                    .build();

            Intent intent = new Intent(activity, GeofenceBroadcastReceiver.class);
            intent.putExtra("lbrName", newLBRItem.title);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_MUTABLE);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestLocationAccess();
                return;
            }
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(activity, unused -> Log.d("COMP3018", "Geofence added"))
                    .addOnFailureListener(activity, e -> Log.d("COMP3018", e.toString()));


            // update UI
            addLBRButton.setEnabled(false);
            reminderEditText.setEnabled(false);
            cancelButton.setEnabled(false);

            // unset newLBR but don't remove it from the map
            newLBR = null;

            Toast.makeText(getActivity(), "Added LBR", Toast.LENGTH_SHORT).show();
        });

        cancelButton = view.findViewById(R.id.map_cancel);
        cancelButton.setOnClickListener(v -> {
            // get rid of current circle
            newLBR.remove();
            newLBR = null;

            // update UI
            reminderEditText.getText().clear();
            addLBRButton.setEnabled(false);
            reminderEditText.setEnabled(false);
            cancelButton.setEnabled(false);
        });

        Button clearLBRs = view.findViewById(R.id.map_clear_lbrs);
        clearLBRs.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                .setTitle("Delete all LBRs")
                .setMessage("This action will delete all location-based reminders. This cannot be undone.")
                .setPositiveButton("Confirm", (dialogInterface, i) -> removeLBRsAndGeofences())
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                .create().show());

        reminderEditText = view.findViewById(R.id.map_edit_title);
    }

    /**
     * Method to remove all location-based reminders and Geofences.
     */
    private void removeLBRsAndGeofences() {
        // delete all LBR rows
        LogRoomDatabase.databaseWriteExecutor.execute(() -> dao.deleteAllLBRs());

        // create pending intent to remove all Geofences
        Intent intent = new Intent(activity, GeofenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_MUTABLE);
        LocationServices.getGeofencingClient(activity).removeGeofences(pendingIntent);
    }
}
