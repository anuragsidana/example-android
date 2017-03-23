package com.hypertrack.example_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.internal.consumer.view.HyperTrackMapFragment;

import java.util.ArrayList;

/**
 * Created by piyush on 04/11/16.
 */
public class UserMapActivity extends AppCompatActivity {

    ArrayList<String> actions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        Intent intent = getIntent();
        if (intent != null) {
            actions = intent.getStringArrayListExtra("actions");
        }

        // Initialize Map Fragment added in Activity Layout to getMapAsync
        // Once map is created onMapReady callback will be fire with GoogleMap object
        HyperTrackMapFragment htMapFragment = (HyperTrackMapFragment) getSupportFragmentManager().findFragmentById(R.id.htMapfragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HyperTrack.removeActions(actions);
    }
}
