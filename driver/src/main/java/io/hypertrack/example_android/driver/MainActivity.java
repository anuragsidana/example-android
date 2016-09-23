package io.hypertrack.example_android.driver;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import io.hypertrack.lib.common.model.HTTask;
import io.hypertrack.lib.transmitter.model.HTTaskParams;
import io.hypertrack.lib.transmitter.model.HTTaskParamsBuilder;
import io.hypertrack.lib.transmitter.model.callback.HTCompleteTaskStatusCallback;
import io.hypertrack.lib.transmitter.model.callback.HTTaskStatusCallback;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

public class MainActivity extends AppCompatActivity {

    private Button startTaskButton;
    private Button completeTaskButton;

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

        // Initialize UI Views
        startTaskButton = (Button) findViewById(R.id.startTaskButton);
        completeTaskButton = (Button) findViewById(R.id.completeTaskButton);

        // Set ClickListeners for UI Elements
        startTaskButton.setOnClickListener(startButtonListener);
        completeTaskButton.setOnClickListener(completeTaskListener);
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
}
