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

import io.hypertrack.example_android.driver.util.BaseActivity;
import io.hypertrack.example_android.driver.util.SharedPreferenceStore;
import io.hypertrack.lib.common.model.HTTask;
import io.hypertrack.lib.transmitter.model.HTShift;
import io.hypertrack.lib.transmitter.model.HTTaskParams;
import io.hypertrack.lib.transmitter.model.HTTaskParamsBuilder;
import io.hypertrack.lib.transmitter.model.callback.HTCompleteTaskStatusCallback;
import io.hypertrack.lib.transmitter.model.callback.HTShiftStatusCallback;
import io.hypertrack.lib.transmitter.model.callback.HTTaskStatusCallback;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

public class MainActivity extends BaseActivity {

    private Button startTaskButton, completeTaskButton;
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
        startTaskButton = (Button) findViewById(R.id.startTaskButton);
        completeTaskButton = (Button) findViewById(R.id.completeTaskButton);
        loadingLayout = (LinearLayout) findViewById(R.id.main_loading_layout);

        // Set ClickListeners for UI Elements
        startTaskButton.setOnClickListener(startButtonListener);
        completeTaskButton.setOnClickListener(completeTaskListener);
    }

    public void proceedToLoginScreen() {
        // On Driver Logout
        SharedPreferenceStore.clearDriverID(getApplicationContext());

        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }

    // Click Listener for StartTask Button
    private View.OnClickListener startButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            HTTaskParamsBuilder htTaskParamsBuilder = new HTTaskParamsBuilder();
            final HTTaskParams htTaskParams = htTaskParamsBuilder
                    .setTaskID(taskID)
                    .setDriverID(driverID)
                    .createHTTaskParams();

            HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());
            transmitterService.startTask(htTaskParams, new HTTaskStatusCallback() {
                @Override
                public void onError(Exception error) {
                    mProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Task start failed: " + error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(boolean isOffline, HTTask task) {
                    mProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Task started: " + task, Toast.LENGTH_SHORT).show();
                }
            });
        }

    };

    // Click Listener for CompleteTask Button
    private View.OnClickListener completeTaskListener = new View.OnClickListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
