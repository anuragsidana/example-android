package io.hypertrack.example_android.driver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import io.hypertrack.lib.transmitter.model.callback.HTEndAllTripsCallback;
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
         * startTrip, completeTask & endTrip calls either with or without user interaction
         * depending on the specific requirements in the workflow of your business and you app.
         */
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * Method to establish DriverSDK connection for a DriverID. Call this method when the DriverSDK connection
         * needs to established, to be able to implement backend-start calls. (Preferably in the onResume()
         * method of your app's Launcher Activity)
         * <p>
         * For more reliability in backend-start calls, call this method just before backend-start need to
         * happen in your app's workflow.
         *
         * For more info refer to the documentation at
         * <a href="http://docs.hypertrack.io/docs/getting-started-android-driver#step-3-for-backend-start-initiate-driversdk-connec">
         *     http://docs.hypertrack.io/docs/getting-started-android-driver#step-3-for-backend-start-initiate-driversdk-connec</a>.
         *
         * For {@link HTTransmitterService} API javadocs, refer to
         * <a href="https://hypertrack.github.io/android-docs/1.4.4/driver/io/hypertrack/lib/transmitter/service/HTTransmitterService.html">
         *     https://hypertrack.github.io/android-docs/1.4.4/driver/io/hypertrack/lib/transmitter/service/HTTransmitterService.html</a>
         */

        if (!TextUtils.isEmpty(driverID)) {
            HTTransmitterService.connectDriver(getApplicationContext(), driverID);
        }
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

        // Initialize EndTrip Button
        Button endTripBtn = (Button) findViewById(R.id.endTripButton);
        if (endTripBtn != null)
            endTripBtn.setOnClickListener(endTripBtnListener);

        Button trackDriverOnMapButton = (Button) findViewById(R.id.trackDriverOnMapButton);
        if (trackDriverOnMapButton != null)
            trackDriverOnMapButton.setOnClickListener(trackDriverOnMapBtnClickListener);

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
                        SharedPreferenceStore.setTripID(MainActivity.this, htTrip.getId());
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

    // Click Listener for EndTrip Button
    private View.OnClickListener endTripBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());

            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            // Check if TripID is available in the app
            String tripID = SharedPreferenceStore.getTripID(MainActivity.this);
            if (!TextUtils.isEmpty(tripID)) {
                /**
                 * Call {@link HTTransmitterService#endTrip(String, HTTripStatusCallback)} method in case
                 * **Trip Id** has been passed on to the driver app by your backend or there are
                 * multiple trips active for the driver. This method will end this trip for the given driver.
                 */
                transmitterService.endTrip(tripID, new HTTripStatusCallback() {
                    @Override
                    public void onSuccess(boolean isOffline, HTTrip htTrip) {
                        mProgressDialog.dismiss();
                        if (isOffline) {
                            Toast.makeText(MainActivity.this, "Trip ended offline for Driver ID: " + driverID, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Trip ended: " + htTrip, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Trip end failed: " + e, Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                /**
                 * Call {@link HTTransmitterService#endAllTrips(String, HTEndAllTripsCallback)} method in case
                 * **Trip Id** is not available on the app. This method will ends all active trips for
                 * the given driver.
                 */
                transmitterService.endAllTrips(driverID, new HTEndAllTripsCallback() {
                    @Override
                    public void onSuccess() {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Trip ended for Driver ID: " + driverID, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Trip end failed: " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    private View.OnClickListener trackDriverOnMapBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Check if Driver is Active currently
            HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());
            if (!transmitterService.isDriverLive()) {

                // Driver is INACTIVE, Dialog to proceed to DriverMap Screen
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.track_driver_on_map_dialog_title);
                builder.setPositiveButton(MainActivity.this.getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                proceedToDriverMapScreen();
                            }
                        });
                builder.setNegativeButton(MainActivity.this.getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
                return;
            }

            // Driver is ACTIVE, Proceed to DriverMapScreen
            proceedToDriverMapScreen();
        }
    };

    private void proceedToDriverMapScreen() {
        Intent driverMapIntent = new Intent(MainActivity.this, DriverMapActivity.class);
        startActivity(driverMapIntent);
    }

    public void onLogoutClicked(MenuItem menuItem) {
        loadingLayout.setVisibility(View.VISIBLE);

        HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());

        String shiftID = SharedPreferenceStore.getShiftID(this);
        if (!TextUtils.isEmpty(shiftID)) {
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

                    // TODO: 04/11/16 Add proper exceptions which can be handled
                    if (e.getMessage().equalsIgnoreCase("Cannot end shift. No active shift.")) {
                        proceedToLoginScreen();
                        return;
                    }
                    Toast.makeText(MainActivity.this, R.string.main_logout_error_shift_end_failed_msg
                            + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        proceedToLoginScreen();
    }

    public void proceedToLoginScreen() {
        loadingLayout.setVisibility(View.GONE);

        // Check if driver is currently active
        HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());
        if (transmitterService.isDriverLive()) {
            Toast.makeText(MainActivity.this, R.string.main_logout_error_active_trip_msg, Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear Saved Data on Driver Logout
        SharedPreferenceStore.clearDriverID(getApplicationContext());
        SharedPreferenceStore.clearShiftID(getApplicationContext());
        SharedPreferenceStore.clearTripID(getApplicationContext());

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
