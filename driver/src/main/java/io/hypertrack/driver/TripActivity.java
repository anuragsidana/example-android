package io.hypertrack.driver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import io.hypertrack.lib.common.HyperTrack;
import io.hypertrack.lib.transmitter.model.HTTrip;
import io.hypertrack.lib.transmitter.model.HTTripParams;
import io.hypertrack.lib.transmitter.model.HTTripParamsBuilder;
import io.hypertrack.lib.transmitter.model.callback.HTCompleteTaskStatusCallback;
import io.hypertrack.lib.transmitter.model.callback.HTTripStatusCallback;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

public class TripActivity extends AppCompatActivity {

    private String taskID = "<YOUR_TASK_ID>";
    private HTTransmitterService mTransmitterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.initializeHyperTrack();
        this.mTransmitterService = HTTransmitterService.getInstance(getApplicationContext());
    }

    private void initializeHyperTrack() {
        HyperTrack.setPublishableApiKey("<YOUR_PUBLISHABLE_KEY>", getApplicationContext());
    }

    // Start Trip

    public void tripStart(View view) {

        ArrayList<String> taskIDs = new ArrayList<>();
        taskIDs.add(taskID);

        HTTripParams tripParams = new HTTripParamsBuilder().setDriverID("<YOUR_DRIVER_ID>")
                .setTaskIDs(taskIDs)
                .createHTTripParams();

        this.mTransmitterService.startTrip(tripParams, new HTTripStatusCallback() {
            @Override
            public void onSuccess(HTTrip htTrip) {
                onTripStart(htTrip);
            }

            @Override
            public void onError(Exception e) {
                onTripStartError(e);
            }
        });
    }

    private void onTripStartError(Exception e) {
        Toast.makeText(getApplicationContext(), "Trip Start Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    private void onTripStart(HTTrip trip) {
        Toast.makeText(getApplicationContext(), "Trip Start : " + trip.toString(), Toast.LENGTH_LONG).show();
    }

    // End Trip

    public void tripEnd(View view) {

        this.mTransmitterService.endTrip(new HTTripStatusCallback() {
            @Override
            public void onSuccess(HTTrip htTrip) {
                onTripEnd(htTrip);
            }

            @Override
            public void onError(Exception e) {
                onTripEndError(e);
            }
        });

    }

    private void onTripEndError(Exception e) {
        Toast.makeText(getApplicationContext(), "Trip End Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    private void onTripEnd(HTTrip trip) {
        Toast.makeText(getApplicationContext(), "Trip End : " + trip.toString(), Toast.LENGTH_LONG).show();
    }

    // Complete Task

    public void completeTask(View view) {

        this.mTransmitterService.completeTask(taskID, new HTCompleteTaskStatusCallback() {
            @Override
            public void onSuccess(String s) {
                onTaskComplete(s);
            }

            @Override
            public void onError(Exception e) {
                onTaskCompleteError(e);
            }
        });
    }

    private void onTaskCompleteError(Exception e) {
        Toast.makeText(getApplicationContext(), "Task Complete Error :  " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    private void onTaskComplete(String taskID) {
        Toast.makeText(getApplicationContext(), "Task Complete : " + taskID, Toast.LENGTH_LONG).show();
    }
}
