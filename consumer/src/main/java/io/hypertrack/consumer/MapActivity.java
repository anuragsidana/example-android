package io.hypertrack.consumer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import io.hypertrack.lib.consumer.model.HTTask;
import io.hypertrack.lib.consumer.network.HTConsumerClient;
import io.hypertrack.lib.consumer.view.HTMapFragment;
import io.hypertrack.lib.consumer.view.HTMapViewClickListener;

public class MapActivity extends AppCompatActivity implements HTMapViewClickListener {

    private HTMapFragment mMapFragment;
    private HTConsumerClient mConsumerClient;
    private BroadcastReceiver mStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.mConsumerClient = HTConsumerClient.getInstance(getApplicationContext());
        this.setupMapFragment();
        this.registerForStatusChangedBroadcast();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.mConsumerClient != null) {
            this.setActionBarTitle(this.mConsumerClient.getStatus());
        }
    }

    private void registerForStatusChangedBroadcast() {
        IntentFilter intentFilter = new IntentFilter(HTConsumerClient.TASK_STATUS_CHANGED_NOTIFICATION);

        mStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mConsumerClient != null) {
                    setActionBarTitle(mConsumerClient.getStatus());
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mStatusReceiver, intentFilter);
    }

    private void setupMapFragment() {
        this.mMapFragment = (HTMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);

        if (this.mMapFragment != null) {
            this.mMapFragment.setCTAButtonVisibility(true);
            this.mMapFragment.setTrafficVisibility(false);
            this.mMapFragment.setDelegate(this);
            this.mMapFragment.setTaskInfoVisibility(true);
        }
    }

    @Override
    public void onCallButtonClicked(HTMapFragment mapFragment) {
        String phoneNumber = this.mConsumerClient.getDriver().getPhone();

        // Handle on call button on click for phone number.
    }

    @Override
    public void onOrderDetailsButtonClicked(HTMapFragment mapFragment) {
        // Show order details
    }

    private void setActionBarTitle(String status) {

        if (status == null)
            return;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setTitle(this.getTitle(status));
    }

    private String getTitle(String status) {
        String title = "NOT STARTED";

        if (status.equalsIgnoreCase(HTTask.TASK_STATUS_DRIVER_ON_THE_WAY)) {
            title = "ON THE WAY";
        }

        if (status.equalsIgnoreCase(HTTask.TASK_STATUS_DRIVER_ARRIVING)) {
            title = "ARRIVING";
        }

        if (status.equalsIgnoreCase(HTTask.TASK_STATUS_DRIVER_ARRIVED)) {
            title = "ARRIVED";
        }

        if (status.equalsIgnoreCase(HTTask.TASK_STATUS_COMPLETED)) {
            title = "DELIVERED";
        }

        if (status.equalsIgnoreCase(HTTask.TASK_STATUS_DISPATCHING)) {
            title = "LEAVING NOW";
        }

        return title;
    }
}
