package com.example.mdp_cw2.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mdp_cw2.R;
import com.example.mdp_cw2.database.LogDao;
import com.example.mdp_cw2.database.LogRoomDatabase;

public class AddAnnotationActivity extends AppCompatActivity {
    private int logId;
    private EditText editDesc;
    private LogDao logDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_annotation);

        logDao = LogRoomDatabase.getDatabase(getApplicationContext()).logDao();

        Intent intent = getIntent();
        logId = intent.getIntExtra("logIndex", -1);
        if(logId == -1) {
            // TODO - some error handling
            Log.d("COMP3018", "ID NOT THERE");
            return;
        }

        editDesc = findViewById(R.id.add_annotation_edit_description);
        Button getImage = findViewById(R.id.add_annotation_get_image);
        Button submit = findViewById(R.id.add_annotation_submit);

        getImage.setOnClickListener(l -> {
            // TODO - implement image path stuff
            Toast.makeText(this, "Unimplemented :(", Toast.LENGTH_SHORT).show();
        });

        submit.setOnClickListener(l -> {
            // TODO - figure out image path stuff
            logDao.updateAnnotation(logId, editDesc.toString(), "");
            finish();
        });
    }
}
