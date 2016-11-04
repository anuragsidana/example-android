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
        HyperTrack.setPublishableApiKey("pk_23a36edd5ae63e0a1b328058dc01f9c2ea6b8cab", getApplicationContext());
        HTTransmitterService.initHTTransmitter(getApplicationContext());
    }
}
