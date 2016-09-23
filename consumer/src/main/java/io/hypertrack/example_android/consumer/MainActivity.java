package io.hypertrack.example_android.consumer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.hypertrack.lib.common.HyperTrack;
import io.hypertrack.lib.consumer.network.HTConsumerClient;
import io.hypertrack.lib.consumer.view.HTMapFragment;

public class MainActivity extends AppCompatActivity {

    private HTMapFragment htMapFragment;
    private String taskID = "YOUR_TASK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HyperTrack.setPublishableApiKey("YOUR_PK", getApplicationContext());
        HTConsumerClient.initHTConsumerClient(getApplicationContext());

        HTConsumerClient.getInstance(getApplicationContext());

        htMapFragment = (HTMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.htMapfragment);
    }
}
