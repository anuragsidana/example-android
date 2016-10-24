# HyperTrack example Android apps
[![Slack Status](http://slack.hypertrack.io/badge.svg)](http://slack.hypertrack.io)

Example application built with the HyperTrack `TransmitterSDK` and `ConsumerSDK` for Android. For the iOS equivalent, refer to [example-io](https://github.com/hypertrack/example-ios).

## Example modules
1. Driver - example of integration with `TransmitterSDK`, which transmits location data for HyperTrack Trips/Shifts
2. Consumer - example of integration with `ConsumerSDK`, which allows real-time tracking of HyperTrack Tasks on a map

## Requirements
1. Android studio with emulator (or test device)
2. HyperTrack API keys. Learn more about them [here](http://docs.hypertrack.io/docs/get-api-keys).

## Usage: Driver
![Driver example](readme-imgs/driver.gif)

The example Driver application implements both Shifts and Trips. Read our [definitions](http://docs.hypertrack.io/docs/definitions) to know more.

Recommended: Take a look at the Android driver [quickstart](http://docs.hypertrack.io/docs/start-a-trip-in-android).

**STEPS**

1. Sync the Gradle files to download the HyperTrack SDKs.
2. Replace `YOUR_PUBLISHABLE_KEY` with your key in `DriverAppApplication.java`.
3. Replace `YOUR_DRIVER_ID` and `YOUR_TASK_ID` with the respective ids in `LoginActivity.java` for Shift and `MainActivity.java` for Trip.
4. Run in emulator. You might need to enable the location permission: see [how](readme-imgs/location.gif).

## Usage: Consumer
![Consumer example](readme-imgs/consumer.gif)

The example Consumer application tracks one Task on a map. Read our [definitions](http://docs.hypertrack.io/docs/definitions) to know more.

Recommended: Take a look at the Android consumer [quickstart](http://docs.hypertrack.io/docs/track-a-task-in-android).

**STEPS**

1. Sync the Gradle files to download the HyperTrack SDKs.
2. Replace `YOUR_PUBLISHABLE_KEY`, with your key, and `YOUR_TASK_ID` with the id of the Task to be tracked in `MainActivity.java`.
3. Run in emulator.

## Documentation
For detailed documentation of the methods and customizations, please visit the official [docs](https://docs.hypertrack.io/).

## Contribute
Please use the [issues tracker](https://github.com/hypertrack/example-android/issues) to raise bug reports and feature requests. We'd love to see your pull requests - send them in!

## Support
Join our [Slack community](http://slack.hypertrack.io) for instant responses. You can also email us at help@hypertrack.io
