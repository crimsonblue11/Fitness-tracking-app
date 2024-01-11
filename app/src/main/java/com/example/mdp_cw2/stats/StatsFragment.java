package com.example.mdp_cw2.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mdp_cw2.R;
import com.example.mdp_cw2.home.AppFragment;

public class StatsFragment extends AppFragment {
    private TextView totalDistance;
    private TextView totalTime;
    private TextView averageDistance;
    private TextView averageTime;
    private TextView numLBRs;
    private TextView numAnnotations;
    private Button viewPrev;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalDistance = view.findViewById(R.id.stats_total_distance);
        totalTime = view.findViewById(R.id.stats_total_time);
        averageDistance = view.findViewById(R.id.stats_avg_distance);
        averageTime = view.findViewById(R.id.stats_avg_time);
        numLBRs = view.findViewById(R.id.stats_num_lbrs);
        numAnnotations = view.findViewById(R.id.stats_num_annotations);
        viewPrev = view.findViewById(R.id.stats_view_prev);
    }
}
