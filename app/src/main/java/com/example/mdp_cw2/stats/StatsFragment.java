/**
 * Fragment showing statistics page.
 */

package com.example.mdp_cw2.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mdp_cw2.AppViewModel;
import com.example.mdp_cw2.MainActivity;
import com.example.mdp_cw2.R;
import com.example.mdp_cw2.Util;
import com.example.mdp_cw2.database.LogItem;
import com.example.mdp_cw2.home.AppFragment;

public class StatsFragment extends AppFragment {
    /**
     * View showing the total distance tracked in the last 30 days.
     */
    private TextView totalDistance;

    /**
     * View showing the total time spent tracking activities in the last 30 days.
     */
    private TextView totalTime;

    /**
     * View showing the average distance per log from the last 30 days.
     */
    private TextView averageDistance;

    /**
     * View showing the average time per log from the last 30 days.
     */
    private TextView averageTime;

    /**
     * View showing the total number of location-based reminders.
     */
    private TextView numLBRs;

    /**
     * View showing the total number of annotations on logs from the last 30 days.
     */
    private TextView numAnnotations;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        AppViewModel viewModel = activity.getViewModel();

        // retrieve everything
        totalDistance = view.findViewById(R.id.stats_total_distance);
        totalTime = view.findViewById(R.id.stats_total_time);
        averageDistance = view.findViewById(R.id.stats_avg_distance);
        averageTime = view.findViewById(R.id.stats_avg_time);
        numLBRs = view.findViewById(R.id.stats_num_lbrs);
        numAnnotations = view.findViewById(R.id.stats_num_annotations);

        // observe LiveData so everything is updated automatically
        viewModel.getAllLogs().observe(activity, logItems -> {
            int totalD = 0;
            int totalT = 0;
            int annotationCount = 0;

            for(LogItem item : logItems) {
                totalD += item.distance;
                totalT += (item.endTime - item.startTime) / 1000;
                if(!item.annotationText.equals("")) {
                    // only increment if there is an annotation
                    annotationCount += 1;
                }
            }

            int avgD = 0;
            int avgT = 0;
            if(logItems.size() != 0) {
                // to avoid divide by zero error
                avgD = totalD / logItems.size();
                avgT = totalT / logItems.size();
            }

            totalTime.setText(Util.secondsToTimeStamp(totalT));
            totalDistance.setText(String.format("%s km", totalD));

            averageTime.setText(Util.secondsToTimeStamp(avgT));
            averageDistance.setText(String.format("%s km", avgD));

            numAnnotations.setText(String.valueOf(annotationCount));
        });

        viewModel.getLBRs().observe(activity, lbrItems -> numLBRs.setText(String.valueOf(lbrItems.size())));
    }
}
