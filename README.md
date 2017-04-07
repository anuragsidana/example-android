# HyperTrack example Android apps
[![Slack Status](http://slack.hypertrack.com/badge.svg)](http://slack.hypertrack.com)

Example application built with the `HyperTrack SDK` for Android. For the iOS equivalent, refer to [example-ios](https://github.com/hypertrack/example-ios).

## Example modules
1. app - example of integration with `HyperTrack SDK`, which transmits location data for HyperTrack User

## Requirements
1. [Android Studio](https://developer.android.com/studio/index.html) with emulator (or test device)
2. HyperTrack API keys. Learn more about them [here](https://docs.hypertrack.com/v3/gettingstarted/authentication.html), and sign up for them [here](https://dashboard.hypertrack.com/signup).

## Usage:
![Driver example](readme-imgs/driver.gif)

The example application implements tracking a User in the background as well as tracking a User on an Action. Read our [introduction](https://docs.hypertrack.com/) to know more.

Recommended: Take a look at the Android [documentation](https://docs.hypertrack.com/v3/sdks/android/installing.html).

**STEPS**

1. Sync the Gradle files to download the HyperTrack SDKs.
2. Replace `YOUR_PUBLISHABLE_KEY` with your Hypertrack API key in `ExampleAppApplication.java` [here](https://github.com/hypertrack/example-android/blob/master/app/src/main/java/com/hypertrack/example_android/ExampleAppApplication.java#L19).
3. Setup a Google Maps API key. More info [here](https://developers.google.com/maps/documentation/android-api/start) and the steps to get an API key are [here](https://developers.google.com/maps/documentation/android-api/start#step_4_get_a_google_maps_api_key).
4. Add the API key to the manifest [here](https://github.com/hypertrack/example-android/blob/master/app/src/main/AndroidManifest.xml#L46).
5. Run in emulator or directly on a phone either by connecting to your computer or by creating an APK and then installing it manually. You might need to enable the location permission: see [how](readme-imgs/location.gif). You also will need to follow [these steps](https://developer.android.com/studio/run/emulator.html#extended) for the emulator.
6. Once you assign an Action on the app you will see that appear on the [Dashboard](https://dashboard.hypertrack.com/live/actions). You will also be able to view a replay of the user's travels.

## Documentation
For detailed documentation of the methods and customizations, please visit the official [docs](https://docs.hypertrack.com/).

## Contribute
Please use the [issues tracker](https://github.com/hypertrack/example-android/issues) to raise bug reports and feature requests. We'd love to see your pull requests - send them in!

## Support
Join our [Slack community](http://slack.hypertrack.com) for instant responses. You can also email us at help@hypertrack.com.
