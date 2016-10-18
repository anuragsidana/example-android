# HyperTrack Android Example application
[![Slack Status](http://slack.hypertrack.io/badge.svg)](http://slack.hypertrack.io)

Example application built with the HyperTrack Driver and Consumer SDKs.

## Example modules
1. Driver - example of integration with `TransmitterSDK`, which transmits location data for HyperTrack Trips/Shifts
2. Consumer - example of integration with `ConsumerSDK`, which allows real-time tracking of HyperTrack Tasks on a map

## Requirements
1. Android studio with emulator or test device
2. HyperTrack keys

## Usage: Driver
![Driver example](readme-imgs/driver.gif)

The example Driver application supports both Shifts and Trips. If you've seen the [quickstart](http://docs.hypertrack.io/docs/start-a-trip-in-android), this will be fairly easy.

1. Gradle sync to get the hypertrack SDK
2. Update pk in DriverAppApplication.java
3. driver_id, task_id in LoginActivity for Shift and MainActivity for Trip
4. might have to enable location permission when running in the emulator

## Usage: Consumer
![Consumer example](readme-imgs/consumer.gif)

The example Consumer application tracks one Task on a map. If you've seen the [quickstart](http://docs.hypertrack.io/docs/track-a-task-in-android), this will be fairly easy.

1. Gradle sync
2. Update pk and task_id to track in MainActivity
3. Run in emulator

## Documentation
For detailed documentation of the methods available, please visit the official [documentation](https://docs.hypertrack.io/).

## Contribute
Please use the [issues tracker](https://github.com/hypertrack/example-android/issues) to raise bug reports and feature requests. We'd love to see your pull requests - send them in!

## Support
Join our [Slack community](http://slack.hypertrack.io) for instant responses. You can also email us at help@hypertrack.io
