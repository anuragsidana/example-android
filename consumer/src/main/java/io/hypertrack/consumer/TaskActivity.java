package io.hypertrack.consumer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.hypertrack.lib.common.HyperTrack;
import io.hypertrack.lib.consumer.model.HTTask;
import io.hypertrack.lib.consumer.model.HTTaskCallBack;
import io.hypertrack.lib.consumer.network.HTConsumerClient;

public class TaskActivity extends AppCompatActivity {

    private HTConsumerClient mConsumerClient;
    private Button mShowMapBtn;
    private TextView mStatusTextView;
    private EditText mTaskIDEditText;
    private BroadcastReceiver mStatusUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.initializeHyperTrack();
        this.mConsumerClient = HTConsumerClient.getInstance(getApplicationContext());

        this.setupViews();
    }

    private void initializeHyperTrack() {
        HyperTrack.setPublishableApiKey("<YOUR_PUBLISHABLE_KEY>", getApplicationContext());
    }

    private void setupViews() {
        this.setupShowMapButton();
        this.setupStatusTextView();
        this.setupTaskIDEditText();
    }

    private void setupShowMapButton() {
        this.mShowMapBtn = (Button) findViewById(R.id.status_btn);
        this.hideMapButton();
    }

    private void hideMapButton() {
        if (this.mShowMapBtn != null) {
            this.mShowMapBtn.setVisibility(View.GONE);
            this.mShowMapBtn.setClickable(false);
        }
    }

    private void showMapButton() {
        if (this.mShowMapBtn != null) {
            this.mShowMapBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setupStatusTextView() {
        this.mStatusTextView = (TextView) findViewById(R.id.status_txt_view);
        this.hideStatusTextView();
    }

    private void hideStatusTextView() {
        if (this.mStatusTextView != null) {
            this.mStatusTextView.setVisibility(View.GONE);
        }
    }

    private void showStatusTextView() {
        if (this.mStatusTextView != null) {
            this.mStatusTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setupTaskIDEditText() {
        this.mTaskIDEditText = (EditText) findViewById(R.id.enter_task_id_edit_txt);
    }

    public void goBtnOnClick(View view) {
        if (this.mTaskIDEditText == null) {
            return;
        }

        String taskID = this.mTaskIDEditText.getText().toString();
        if (TextUtils.isEmpty(taskID)) {
            Toast.makeText(getApplicationContext(), "Enter valid task id", Toast.LENGTH_LONG).show();
        }

        this.hideMapButton();
        this.hideStatusTextView();

        this.mConsumerClient.trackTask(taskID, this, new HTTaskCallBack() {
            @Override
            public void onSuccess(HTTask htTask) {
                onTrackTaskSuccess(htTask);
            }

            @Override
            public void onError(Exception e) {
                onTrackTaskError(e);
            }
        });
    }

    private void onTrackTaskSuccess(HTTask task) {
        this.registerForStatusUpdatesBroadcast();

        this.showMapButton();
        this.showStatusTextView();

        this.updateMapButton(task.getStatus());
    }

    private void updateMapButton(String status) {
        if (status == null)
            return;

        if (status.equalsIgnoreCase(HTTask.TASK_STATUS_DRIVER_ON_THE_WAY)
                || status.equalsIgnoreCase(HTTask.TASK_STATUS_DRIVER_ARRIVING)
                || status.equalsIgnoreCase(HTTask.TASK_STATUS_DRIVER_ARRIVED)) {
            mShowMapBtn.setBackgroundColor(0xFF9C27B0);
            mShowMapBtn.setClickable(true);
            mShowMapBtn.setText("TRACK");
        }

        if (status.equalsIgnoreCase(HTTask.TASK_STATUS_COMPLETED)) {
            mShowMapBtn.setBackgroundColor(0xFF9C27B0);
            mShowMapBtn.setClickable(true);
            mShowMapBtn.setText("DELIVERED");
        }

        if (status.equalsIgnoreCase(HTTask.TASK_STATUS_DISPATCHING)) {
            mShowMapBtn.setBackgroundColor(0xFF9C27B0);
            mShowMapBtn.setClickable(true);
            mShowMapBtn.setText("LEAVING NOW");
        }

        if (status.equalsIgnoreCase(HTTask.TASK_STATUS_NOT_STARTED)) {
            mShowMapBtn.setBackgroundColor(0xFF757575);
            mShowMapBtn.setClickable(false);
            mShowMapBtn.setText("NOT STARTED");
        }
    }

    private void registerForStatusUpdatesBroadcast() {
        IntentFilter intentFilter = new IntentFilter(HTConsumerClient.TASK_STATUS_CHANGED_NOTIFICATION);
        mStatusUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateMapButton(mConsumerClient.getStatus());
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mStatusUpdateReceiver, intentFilter);
    }

    private void onTrackTaskError(Exception e) {
        Toast.makeText(getApplicationContext(), "Enter valid task id", Toast.LENGTH_LONG).show();
    }

    public void showMapBtnOnClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
