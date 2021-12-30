# mobile_smart_watch

A new Flutter project.

## Getting Started

This project is a starting point for a Flutter
[plug-in package](https://flutter.dev/developing-packages/),
a specialized package that includes platform-specific implementation code for
Android and/or iOS.

For help getting started with Flutter, view our
[online documentation](https://flutter.dev/docs), which offers tutorials,
samples, guidance on mobile development, and a full API reference.

## Flutter Plugin Implementation Steps
Steps to be followed for flutter android implementaion.

1. Make sure your flutter android application minSdkVersion is > = 18.
2. Make sure the below permissions are added in the android manifest.xml file.
```
 <uses-permission android:name="android.permission.INTERNET" />

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

    <!-- NRF upgrade required -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />

    <!-- The custom dial of the demo needs to take photos to replace the background-->
    <uses-permission android:name="android.permission.CAMERA" />

    <uses-feature android:name="android.hardware.bluetooth" android:required="true"/>
    <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>

    <uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />

    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <!-- Needed only if your app makes the device discoverable to Bluetooth devices. -->
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
    <!-- Needed only if your app communicates with already-paired Bluetooth devices. -->
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```
3.

## Notes if any issues:
* If we get any issue with the dependencies as following then we have to add few changes into our project.
1.  If you face any issue like "could not find sdk", then add the .aar file from the plugin libs folder into your application/android/app/libs/ directory.
```
> Could not resolve all files for configuration ':app:debugRuntimeClasspath'.
   > Could not find :ute_sdk:.
     Searched in the following locations:
       - file:/XYZ/android/app/libs/ute_sdk.aar
```

2. If you are facing any issue like "Attribute application@label value=(application_name) from AndroidManifest.xml then add the following line for application tag into androoid amnifest.xml file.

```
add 'tools:replace="android:label"' into <application> tag of android manifest
```

3. if you are facing any issue like
```
dart pub global activate devtools -v 2.8.0 or flutter global activate devtools -v 2.8.0
```