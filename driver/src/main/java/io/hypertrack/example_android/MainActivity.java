package io.hypertrack.example_android;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import io.hypertrack.lib.common.HyperTrack;
import io.hypertrack.lib.common.model.HTTask;
import io.hypertrack.lib.transmitter.model.HTTaskParams;
import io.hypertrack.lib.transmitter.model.HTTaskParamsBuilder;
import io.hypertrack.lib.transmitter.model.callback.HTCompleteTaskStatusCallback;
import io.hypertrack.lib.transmitter.model.callback.HTTaskStatusCallback;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

public class MainActivity extends AppCompatActivity {

    private Button startTaskButton;
    private Button completeTaskButton;

    private String taskID = "YOUR_TASK_ID";
    private String driverID = "YOUR_DRIVER_ID";

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTaskButton = (Button) findViewById(R.id.startTaskButton);
        startTaskButton.setOnClickListener(startButtonListener);
        completeTaskButton = (Button) findViewById(R.id.completeTaskButton);
        completeTaskButton.setOnClickListener(completeTaskListener);

        HyperTrack.setPublishableApiKey("YOUR_PK", getApplicationContext());
        HTTransmitterService.initHTTransmitter(getApplicationContext());
    }


    View.OnClickListener startButtonListener = new View.OnClickListener() {
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

    View.OnClickListener completeTaskListener = new View.OnClickListener() {
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
