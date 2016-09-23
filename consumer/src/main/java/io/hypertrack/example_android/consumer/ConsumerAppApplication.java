package io.hypertrack.example_android.consumer;

import android.app.Application;

import io.hypertrack.lib.common.HyperTrack;
import io.hypertrack.lib.consumer.network.HTConsumerClient;

/**
 * Created by piyush on 23/09/16.
 */
public class ConsumerAppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Set HyperTrack Publishable Key & Initialize TransmitterSDK
        HyperTrack.setPublishableApiKey("<YOUR_PUBLISHABLE_KEY_HERE>", getApplicationContext());
        HTConsumerClient.initHTConsumerClient(getApplicationContext());
    }
}
