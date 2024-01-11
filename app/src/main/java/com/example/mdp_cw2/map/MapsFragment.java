package com.example.mdp_cw2.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.room.RoomDatabase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mdp_cw2.R;
import com.example.mdp_cw2.database.LBRItem;
import com.example.mdp_cw2.database.LogDao;
import com.example.mdp_cw2.database.LogRoomDatabase;
import com.example.mdp_cw2.home.AppFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends AppFragment {
    private LogDao dao;
    private boolean isAddingLBR = false;
    private Button addLBRButton;
    final ArrayList<LBRItem> LBRs = new ArrayList<>();

    private CircleOptions newLBR;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            for (LBRItem lbr : LBRs) {
                googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(lbr.latitude, lbr.longitude))
                        .radius(lbr.radius)
                        .clickable(true)
                        .fillColor(R.color.black)
                        .visible(true)
                );
            }

            Log.d("COMP3018", "" + LBRs.size());
            if (!LBRs.isEmpty()) {
                LatLng firstLBR = new LatLng(LBRs.get(0).latitude, LBRs.get(0).longitude);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(firstLBR));
            }

            googleMap.setOnMapClickListener(l -> {
                // TODO - better option?
                googleMap.clear();

                newLBR = new CircleOptions()
                        .center(new LatLng(l.latitude, l.longitude))
                        .radius(100)
                        .clickable(true)
                        .fillColor(R.color.black);
                googleMap.addCircle(newLBR);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(l.latitude, l.longitude)));
                isAddingLBR = true;
            });

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
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

        addLBRButton = view.findViewById(R.id.map_add_reminder);
        addLBRButton.setOnClickListener(l -> {

        });

        dao = LogRoomDatabase.getDatabase(getContext()).logDao();
        LogRoomDatabase.databaseWriteExecutor.execute(() -> {
            LBRs.addAll(dao.getAllLBRs());
            Log.d("COMP3018", "Fetched");
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}
