/**
 * RecyclerView adapter for Recent fragment.
 *
 * @see com.example.mdp_cw2.recent.RecentFragment
 */

package com.example.mdp_cw2.recent;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdp_cw2.R;
import com.example.mdp_cw2.database.LogItem;
import com.example.mdp_cw2.database.LogLiked;
import com.example.mdp_cw2.home.AddAnnotationActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LogItemViewAdapter extends RecyclerView.Adapter<LogItemViewAdapter.LogViewHolder> {
    /**
     * ArrayList containing log items.
     */
    private List<LogItem> data = new ArrayList<>();

    /**
     * LayoutInflater object to inflate each object in the adapter.
     */
    private final LayoutInflater layoutInflater;

    /**
     * Constructor method. Fetches layout inflater via context.
     * @param context App context, needed to inflate each log in the adapter.
     */
    public LogItemViewAdapter(Context context) {
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
            int oldDataLength = data.size();
            data.clear();
            data.addAll(newData);
            notifyItemRangeChanged(0, oldDataLength);
        } else {
            data = newData;
        }
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        /**
         * View showing time the log was started at.
         */
        private final TextView timeView;

        /**
         * View showing the date of the log.
         */
        private final TextView dateView;

        /**
         * View showing the LogType of the log.
         */
        private final TextView typeView;

        /**
         * View showing the total time and distance of the log.
         */
        private final TextView distanceAndTimeView;

        /**
         * Button to add annotation to the log.
         */
        private final Button addAnnotation;

        /**
         * View showing the annotated description of the log (if available).
         */
        private final TextView descriptionView;

        /**
         * Layout containing button to add annotation.
         * Disabled when an annotation has already been added.
         */
        private final LinearLayout addAnnotationLayout;

        /**
         * Layout containing annotation data.
         * Disabled if no annotation has been added.
         */
        private final LinearLayout annotationBox;

        /**
         * ImageView containing a thumb up icon.
         * Displayed only when the annotation has been rated "thumbs up".
         */
        private final ImageView thumbUp;

        /**
         * ImageView containing a thumb down icon.
         * Displayed only when the annotation has been rated "thumbs down".
         */
        private final ImageView thumbDown;

        /**
         * Constructor method.
         * @param itemView Parent ItemView object
         */
        public LogViewHolder(View itemView) {
            super(itemView);

            // retrieve everything
            timeView = itemView.findViewById(R.id.list_item_time);
            dateView = itemView.findViewById(R.id.list_item_date);
            typeView = itemView.findViewById(R.id.list_item_type);
            distanceAndTimeView = itemView.findViewById(R.id.list_item_distance_and_time);
            addAnnotation = itemView.findViewById(R.id.list_item_add_annotation);
            descriptionView = itemView.findViewById(R.id.list_item_annotation_description);
            addAnnotationLayout = itemView.findViewById(R.id.add_annotation_box);
            annotationBox = itemView.findViewById(R.id.add_annotation_annotation_box);
            thumbUp = itemView.findViewById(R.id.list_item_thumb_up);
            thumbDown = itemView.findViewById(R.id.list_item_thumb_down);
        }

        public void bind(final LogItem log) {
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

            // timestamps are in ms, so divide by 1000
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
                // annotation is present, so show annotation data
                annotationBox.setVisibility(View.VISIBLE);
                addAnnotationLayout.setVisibility(View.GONE);

                descriptionView.setText(log.annotationText);

                if(log.likedState == LogLiked.THUMB_UP) {
                    thumbUp.setVisibility(View.VISIBLE);
                    thumbDown.setVisibility(View.GONE);
                } else if(log.likedState == LogLiked.THUMB_DOWN) {
                    thumbUp.setVisibility(View.GONE);
                    thumbDown.setVisibility(View.VISIBLE);
                } else {
                    thumbUp.setVisibility(View.GONE);
                    thumbDown.setVisibility(View.GONE);
                }
            } else {
                // no annotation, so show annotation button
                annotationBox.setVisibility(View.GONE);
                addAnnotationLayout.setVisibility(View.VISIBLE);

                addAnnotation.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), AddAnnotationActivity.class);
                    intent.putExtra("logIndex", log.getIndex());
                    v.getContext().startActivity(intent);
                });
            }
        }
    }
}
