package io.hypertrack.example_android.driver;

import android.app.Application;

import io.hypertrack.lib.common.HyperTrack;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

/**
 * Created by piyush on 23/09/16.
 */
public class DriverAppApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // Set HyperTrack Publishable Key & Initialize TransmitterSDK
        HyperTrack.setPublishableApiKey("YOUR_PUBLISHABLE_KEY", getApplicationContext());
        HTTransmitterService.initHTTransmitter(getApplicationContext());

    }
}
