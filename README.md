# HyperTrack Android Example application
[![Slack Status](http://slack.hypertrack.io/badge.svg)](http://slack.hypertrack.io)

Example application built with the HyperTrack Driver and Consumer SDKs.

## Example modules

1. Consumer - example of integration with `ConsumerSDK`
2. Driver - example of integration with `TransmitterSDK`

## Requirements
1. Android studio with emulator or test device
2. HyperTrack keys

## Usage: Driver
![Driver flow](readme-imgs/driver.gif)

The example Driver application supports both Shifts and Trips. If you've seen the Driver App quickstart, this will be fairly easy.

1. Gradle sync to get the hypertrack SDK
2. Update pk in DriverAppApplication.java
3. driver_id, task_id in LoginActivity for Shift and MainActivity for Trip
4. might have to enable location permission when running in the emulator

## Usage: Consumer


## Contribute
