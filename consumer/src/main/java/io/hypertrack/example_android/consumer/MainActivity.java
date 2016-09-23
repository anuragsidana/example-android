package io.hypertrack.example_android.consumer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import io.hypertrack.lib.consumer.network.HTConsumerClient;
import io.hypertrack.lib.consumer.view.HTMapAdapter;
import io.hypertrack.lib.consumer.view.HTMapFragment;
import io.hypertrack.lib.consumer.view.HTMapFragmentCallback;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private HTMapFragment htMapFragment;
    private HTConsumerClient mHTConsumerClient;

    private String taskID = "YOUR_TASK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize HyperTrack Consumer Client (provides access to Tasks being tracked in ConsumerSDK)
        mHTConsumerClient = HTConsumerClient.getInstance(getApplicationContext());

        // Initialize HyperTrack MapFragment in Activity Layout
        htMapFragment = (HTMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.htMapfragment);

        // Set an instance of HTMapAdapter (provides access to customize MapFragment UI)
        HyperTrackMapAdapter adapter = new HyperTrackMapAdapter(this);
        htMapFragment.setHTMapAdapter(adapter);

        // Set an instance of HTMapFragmentCallback (provides access to events happening on MapFragment)
        htMapFragment.setMapFragmentCallback(callback);
    }

    /**
     * HyperTrack Map Adapter to Track Tasks & Customize UI components on HyperTrack MapFragment
     */
    public class HyperTrackMapAdapter extends HTMapAdapter {

        private Context mContext;

        public HyperTrackMapAdapter(Context mContext) {
            super(mContext);
            this.mContext = mContext;
        }

        @Override
        public List<String> getTaskIDsToTrack(HTMapFragment mapFragment) {
            // Return a List of Orders to be tracked on the map here
            ArrayList<String> ordersToBeTracked = new ArrayList<>();
            ordersToBeTracked.add(taskID);

            return ordersToBeTracked;
        }

        @Override
        public String getOrderStatusToolbarDefaultTitle(HTMapFragment mapFragment) {
            // Set Default Toolbar Title as app_name
            return mContext.getString(R.string.app_name);
        }
    }

    /**
     * Callback to get events happening on HyperTrack MapFragment
     */
    private HTMapFragmentCallback callback = new HTMapFragmentCallback() {
        @Override
        public void onMapReadyCallback(HTMapFragment mapFragment, GoogleMap map) {
            super.onMapReadyCallback(mapFragment, map);
        }

        @Override
        public void onMapFragmentSucceed(HTMapFragment mapFragment, List<String> taskIDList) {
            super.onMapFragmentSucceed(mapFragment, taskIDList);

            if (taskIDList != null && taskIDList.size() > 0) {
                Toast.makeText(MainActivity.this, "Started tracking tasks on Map", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onMapFragmentFailed(HTMapFragment mapFragment, List<String> taskIDList, String errorMessage) {
            super.onMapFragmentFailed(mapFragment, taskIDList, errorMessage);

            if (taskIDList != null && taskIDList.size() > 0) {
                Toast.makeText(MainActivity.this, "Error while starting tracking tasks on Map: "
                        + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onHeroMarkerClicked(HTMapFragment mapFragment, String taskID, Marker heroMarker) {
            super.onHeroMarkerClicked(mapFragment, taskID, heroMarker);
            Toast.makeText(MainActivity.this, "Hero Marker Clicked", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Broadcast Receiver to get Task details updates for Tasks being tracked
     */
    BroadcastReceiver mTaskDetailsRefreshedListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.hasExtra(HTConsumerClient.INTENT_EXTRA_TASK_ID_LIST)) {
                ArrayList<String> taskIDList = intent.getStringArrayListExtra(HTConsumerClient.INTENT_EXTRA_TASK_ID_LIST);

                if (taskIDList != null) {
                    for (String taskID : taskIDList) {
                        Log.d(TAG, "Task Details updated for TaskID: " + taskID);
                    }
                }
            }
        }
    };

    /**
     * Broadcast Receiver to get Task status changed updates for Tasks being tracked
     */
    BroadcastReceiver mTaskStatusChangedListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.hasExtra(HTConsumerClient.INTENT_EXTRA_TASK_ID_LIST)) {
                ArrayList<String> taskIDList = intent.getStringArrayListExtra(HTConsumerClient.INTENT_EXTRA_TASK_ID_LIST);

                if (taskIDList != null) {
                    for (String taskID : taskIDList) {

                        if (!TextUtils.isEmpty(mHTConsumerClient.getStatus(taskID))) {
                            Log.d(TAG, "Task Status changed for TaskID: " + taskID + " to: " + mHTConsumerClient.getStatus(taskID));
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        // Register for TasksDetailRefreshed Listener and TasksStatusChanged Listener
        LocalBroadcastManager.getInstance(this).registerReceiver(mTaskDetailsRefreshedListener,
                new IntentFilter(HTConsumerClient.TASK_DETAIL_REFRESHED_NOTIFICATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(mTaskStatusChangedListener,
                new IntentFilter(HTConsumerClient.TASK_STATUS_CHANGED_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister for TasksDetailRefreshed Listener and TasksStatusChanged Listener
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTaskDetailsRefreshedListener);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTaskStatusChangedListener);
    }
}
