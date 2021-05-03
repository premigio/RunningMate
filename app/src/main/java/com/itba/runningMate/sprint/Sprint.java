package com.itba.runningMate.sprint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.itba.runningMate.R;

public class Sprint extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint);


//        final Toolbar toolbar = findViewById(R.id.toolbar);

//        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final String id = getIntent().getData().getQueryParameter("sprintId");
        final TextView text = findViewById(R.id.text_view_sprint);
        text.setText(id);

    }
}