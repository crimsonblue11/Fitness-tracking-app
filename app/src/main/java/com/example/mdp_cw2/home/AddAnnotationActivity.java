/**
 * Activity for adding an annotation to a log
 */

package com.example.mdp_cw2.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.RoomDatabase;

import com.example.mdp_cw2.R;
import com.example.mdp_cw2.database.LogDao;
import com.example.mdp_cw2.database.LogLiked;
import com.example.mdp_cw2.database.LogRoomDatabase;
import com.example.mdp_cw2.database.LogType;

public class AddAnnotationActivity extends AppCompatActivity {
    /**
     * Database ID for the log item being changed.
     */
    private int logId;
    /**
     * "Liked" state of the annotation.
     * Set to THUMB_UP if the thumb up button is clicked.
     * Set to THUMB_DOWN if the thumb down button is clicked.
     */
    private LogLiked logLiked = LogLiked.UNRATED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_annotation);

        LogDao logDao = LogRoomDatabase.getDatabase(getApplicationContext()).logDao();

        Intent intent = getIntent();
        logId = intent.getIntExtra("logIndex", -1);
        if (logId == -1) {
            Toast.makeText(this, "Oops, something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText editDesc = findViewById(R.id.add_annotation_edit_description);
        Button submit = findViewById(R.id.add_annotation_submit);

        ImageButton thumbUpButton = findViewById(R.id.add_annotation_thumb_up);
        ImageButton thumbDownButton = findViewById(R.id.add_annotation_thumb_down);

        thumbUpButton.setOnClickListener(l -> logLiked = LogLiked.THUMB_UP);
        thumbDownButton.setOnClickListener(l -> logLiked = LogLiked.THUMB_DOWN);

        submit.setOnClickListener(l -> {
            // submit new annotation to db and finish activity
            LogRoomDatabase.databaseWriteExecutor.execute(() -> logDao.updateAnnotation(logId, editDesc.getText().toString(), logLiked));
            finish();
        });
    }
}
