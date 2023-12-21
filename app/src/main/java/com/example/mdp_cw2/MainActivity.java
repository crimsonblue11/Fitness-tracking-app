package com.example.mdp_cw2;

import android.os.Bundle;

import com.example.mdp_cw2.home.FragmentManagerActivity;
import com.example.mdp_cw2.home.HomeFragment;
import com.example.mdp_cw2.map.MapFragment;
import com.example.mdp_cw2.recent.RecentFragment;
import com.example.mdp_cw2.stats.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends FragmentManagerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showFirstFragment(new HomeFragment(), R.id.main_frameLayout);

        BottomNavigationView bottomNav = findViewById(R.id.main_bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.bottom_nav_home) {
                // go to home / tracking fragment
                switchFragment(new HomeFragment(), R.id.main_frameLayout);
            } else if(id == R.id.bottom_nav_map) {
                // go to map fragment
                switchFragment(new MapFragment(), R.id.main_frameLayout);
            } else if(id == R.id.bottom_nav_stats) {
                // go to navigation fragment
                switchFragment(new StatsFragment(), R.id.main_frameLayout);
            } else if(id == R.id.bottom_nav_recent) {
                // go to recent logs fragment
                switchFragment(new RecentFragment(), R.id.main_frameLayout);
            }
            return true;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        previousFragment(false);
        return true;
    }
}
