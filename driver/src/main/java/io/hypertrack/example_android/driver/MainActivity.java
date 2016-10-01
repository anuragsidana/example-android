package io.hypertrack.example_android.driver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import io.hypertrack.example_android.driver.util.BaseActivity;
import io.hypertrack.example_android.driver.util.SharedPreferenceStore;
import io.hypertrack.lib.transmitter.model.HTShift;
import io.hypertrack.lib.transmitter.model.HTTrip;
import io.hypertrack.lib.transmitter.model.HTTripParams;
import io.hypertrack.lib.transmitter.model.HTTripParamsBuilder;
import io.hypertrack.lib.transmitter.model.callback.HTCompleteTaskStatusCallback;
import io.hypertrack.lib.transmitter.model.callback.HTShiftStatusCallback;
import io.hypertrack.lib.transmitter.model.callback.HTTripStatusCallback;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

public class MainActivity extends BaseActivity {

    private LinearLayout loadingLayout;

    /**
     * Your ORDER_ID maps to HyperTrack's TASK_ID
     */
    private String taskID = "YOUR_TASK_ID";
    /**
     * DRIVER_ID is received when a Driver entity is created using HyperTrack APIs
     */
    private String driverID = "YOUR_DRIVER_ID";

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if Driver is logged in
        String driverID = SharedPreferenceStore.getDriverID(this);
        if (TextUtils.isEmpty(driverID)) {
            proceedToLoginScreen();
            return;
        }

        initToolbar(getString(R.string.app_name), false);

        // Initialize UI Views
        initUIViews();

        /**
         * @IMPORTANT:
         * Implement Network call to fetch ORDERS/TRANSACTIONS for the DRIVER here.
         * Once the list of orders/transactions have been fetched, implement
         * startTrip, completeTask & completeTrip calls either with or without user interaction
         * depending on the specific requirements in the workflow of your business and you app.
         */
    }

    private void initUIViews() {
        // Initialize StartTrip Button
        Button startTripBtn = (Button) findViewById(R.id.startTripButton);
        if (startTripBtn != null)
            startTripBtn.setOnClickListener(startTripBtnListener);

        // Initialize CompleteTask Button
        Button completeTaskBtn = (Button) findViewById(R.id.completeTaskButton);
        if (completeTaskBtn != null)
            completeTaskBtn.setOnClickListener(completeTaskBtnListener);

        // Initialize CompleteTrip Button
        Button completeTripBtn = (Button) findViewById(R.id.completeTripButton);
        if (completeTripBtn != null)
            completeTripBtn.setOnClickListener(completeTripBtnListener);

        loadingLayout = (LinearLayout) findViewById(R.id.main_loading_layout);
    }

    // Click Listener for StartTrip Button
    private View.OnClickListener startTripBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            ArrayList<String> taskIDs = new ArrayList<>();
            taskIDs.add(taskID);

            HTTripParams htTripParams = new HTTripParamsBuilder().setDriverID(driverID)
                    .setTaskIDs(taskIDs)
                    .setOrderedTasks(false)
                    .setIsAutoEnded(false)
                    .createHTTripParams();

            HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());
            transmitterService.startTrip(htTripParams, new HTTripStatusCallback() {
                @Override
                public void onSuccess(boolean isOffline, HTTrip htTrip) {
                    mProgressDialog.dismiss();
                    if (isOffline) {
                        Toast.makeText(MainActivity.this, "Trip started offline", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Trip started: " + htTrip, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Task start failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    // Click Listener for CompleteTask Button
    private View.OnClickListener completeTaskBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());
            transmitterService.completeTask(taskID, new HTCompleteTaskStatusCallback() {
                @Override
                public void onError(Exception error) {
                    mProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Task complete failed: " + error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String taskID) {
                    mProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Task completed: " + taskID, Toast.LENGTH_SHORT).show();
                }
            });
        }

    };

    // Click Listener for CompleteTrip Button
    private View.OnClickListener completeTripBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());
            if (!transmitterService.isTripActive()) {
                Toast.makeText(MainActivity.this, R.string.main_no_active_trip_msg, Toast.LENGTH_SHORT).show();
                return;
            }

            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            transmitterService.completeTrip(new HTTripStatusCallback() {
                @Override
                public void onError(Exception error) {
                    mProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Trip complete failed: " + error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(boolean isOffline, HTTrip htTrip) {
                    mProgressDialog.dismiss();
                    if (isOffline) {
                        Toast.makeText(MainActivity.this, "Trip completed offline for Driver ID: " + driverID, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Trip completed: " + htTrip, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    };

    public void onLogoutClicked(MenuItem menuItem) {
        HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());
        if (transmitterService.isTripActive()) {
            Toast.makeText(MainActivity.this, R.string.main_logout_error_active_trip_msg, Toast.LENGTH_SHORT).show();
            return;
        }

        if (transmitterService.isShiftActive()) {
            loadingLayout.setVisibility(View.VISIBLE);

            transmitterService.endShift(new HTShiftStatusCallback() {
                @Override
                public void onSuccess(HTShift htShift) {
                    loadingLayout.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, R.string.main_logout_success_msg, Toast.LENGTH_SHORT).show();
                    proceedToLoginScreen();
                }

                @Override
                public void onError(Exception e) {
                    loadingLayout.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, R.string.main_logout_error_shift_end_failed_msg
                            + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            proceedToLoginScreen();
        }
    }

    public void proceedToLoginScreen() {
        // On Driver Logout
        SharedPreferenceStore.clearDriverID(getApplicationContext());

        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
