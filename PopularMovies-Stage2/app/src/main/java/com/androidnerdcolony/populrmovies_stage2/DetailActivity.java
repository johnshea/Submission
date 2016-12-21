package com.androidnerdcolony.populrmovies_stage2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        int movieId = intent.getIntExtra("movieId", 0);

        Toast.makeText(this, "movieId" + movieId + "clicked", Toast.LENGTH_SHORT).show();
    }
}
