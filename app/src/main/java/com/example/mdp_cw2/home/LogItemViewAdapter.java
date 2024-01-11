package com.example.mdp_cw2.home;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdp_cw2.R;
import com.example.mdp_cw2.database.LogItem;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LogItemViewAdapter extends RecyclerView.Adapter<LogItemViewAdapter.LogViewHolder> {
    private List<LogItem> data;
    //    private final Context context;
    private final LayoutInflater layoutInflater;

    public LogItemViewAdapter(Context context, List<LogItem> data) {
        this.data = data;
//        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.log_list_item, parent, false);
        return new LogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<LogItem> newData) {
        if(data != null) {
            data.clear();
            data.addAll(newData);
            // TODO - change
            notifyDataSetChanged();
        } else {
            data = newData;
        }
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView timeView;
        TextView dateView;
        TextView typeView;
        TextView distanceAndTimeView;
        Button addAnnotation;
        TextView descriptionView;

        LinearLayout annotationBox;
        LinearLayout addAnnotationBox;

        LogViewHolder(View itemView) {
            super(itemView);

            // retrieve everything
            timeView = itemView.findViewById(R.id.list_item_time);
            dateView = itemView.findViewById(R.id.list_item_date);
            typeView = itemView.findViewById(R.id.list_item_type);
            distanceAndTimeView = itemView.findViewById(R.id.list_item_distance_and_time);
            addAnnotation = itemView.findViewById(R.id.list_item_add_annotation);
            descriptionView = itemView.findViewById(R.id.list_item_annotation_description);

            annotationBox = itemView.findViewById(R.id.list_item_annotation_box);
            addAnnotationBox = itemView.findViewById(R.id.add_annotation_box);
        }

        void bind(final LogItem log) {
            // set views to data
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(log.startTime);
            String time = DateFormat.format("hh:mma", cal).toString();
            String date = DateFormat.format("dd/MM/yyyy", cal).toString();

            timeView.setText(time);
            dateView.setText(date);

            switch (log.logType) {
                case WALK:
                    typeView.setText(R.string.walk);
                    break;
                case RUN:
                    typeView.setText(R.string.run);
                    break;
                case CYCLE:
                    typeView.setText(R.string.cycle);
                    break;
            }

            int totalTimeSecs = (int) (log.endTime - log.startTime) / 1000;

            int totalTimeMins = totalTimeSecs / 60;
            int totalTimeHours = totalTimeMins / 60;

            String timeString;
            if (totalTimeHours > 0) {
                totalTimeMins -= (totalTimeHours * 60);
                timeString = totalTimeHours + "hr and " + totalTimeMins + " mins";
            } else {
                timeString = totalTimeMins + " mins";
            }

            distanceAndTimeView.setText(String.format("%skm for %s", log.distance, timeString));

            if(!log.annotationText.isEmpty()) {
                // annotation - TEXT ONLY
                Log.d("COMP3018", log.annotationText);

                annotationBox.setVisibility(View.VISIBLE);
                addAnnotationBox.setVisibility(View.GONE);

                descriptionView.setText(log.annotationText);
            } else {
                // NO ANNOTATION
                annotationBox.setVisibility(View.GONE);
                addAnnotationBox.setVisibility(View.VISIBLE);
                addAnnotation.setOnClickListener(v -> Toast.makeText(itemView.getContext(), "Not implemented yet :(", Toast.LENGTH_SHORT).show());
            }

            // TODO - annotation with image
        }
    }
}
