# Architecture specification

System consists of four main elements:
![System overview](architecture/system-overview.png)
* Admin panel
* API endpoint
* Mobile clients
* Camera devices

## Admin panel

**TODO**

## API Endpoint
Server and clients other than web application communicate via REST API. Methods definition (see /specs/api.yaml) conforms to [Open API 2.0 Specification](http://spec.openapis.org/oas/v2.0).

Server exposes API endpoint using Django Rest Framework extension. API definition file is generated from this implementation.

**TODO(Rhantolq):** Where to find generator?

Clients leverage [OpenAPI generator](https://github.com/OpenAPITools/openapi-generator) to generate the API handlers. Generated classes are not included in the repository. Please see client readme for generation instructions.

**TODO(kantoniak):** Generation instructions for cameras

**TODO(avan1235):** Generation instructions for Android

### Server and Android app communication

App loads the data from server in the background asking only for data needed to rpesent on current app screen. The data is refreshed on every screen change in app.

When the connection with internet is lost by the device, app is suspended and waiting screen shows while waiting for active internet connection. The possible connections type supported by app are WiFi, cellular data and VPN connections.

**TODO**: write about user sending data to server from client app

## Mobile client architecture

Client app designed for Android devices is developed in Android Studio in Kotlin and Java. Build process is managed by the Gradle build system. App uses [AndroidX support libraries](https://developer.android.com/jetpack/androidx) to handle android system libraries.

For concurrenrcy purpose the [RxKotlin](https://github.com/ReactiveX/RxKotlin) as the bindings to [RxJava](https://github.com/ReactiveX/RxJava) with [Android extensions](https://github.com/ReactiveX/RxAndroid) is used in project alongsite the standard [Kotlin coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html). In the feature all the asynchronous tasks are to be migrated to couroutines code. 

The app uses the OpenStreet Maps with the MapView designed in [osmdroid library](https://github.com/osmdroid/osmdroid) allowing the developers to customize the map look and easily manage the map data caching.

User interface needs also some data visualizations of all the data so the [data2viz library](https://github.com/data2viz/data2viz) is used to build all the needed plots and graphs from scratch in a Kotlin code and easily place them into the standard Android layouts.

For the RESTful API endpoints generation in Android app the [Kortlin code generator](https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/kotlin.md) is used.

**TODO**: write about handling the diffenrsnt languages in app, maybe somthing else

## Camera devices

Camera devices are Raspberry Pi 3B+ (or newer) boards running [Raspbian Buster Lite](https://www.raspberrypi.org/downloads/raspbian/) February 2020 release. Each device is equipped with a 32GB SD card, WiFi card for internet connection and Camera Module or external USB webcam.

![Camera Device Software](architecture/camera-devices.png)

Device software consists of two parts:

* setup flow for registering credentials in device memory,
* main script for running analysis.

Both elements are Python 3.7 scripts with Python language bindings from other software like OpenCV.

Setup flow saves user credentials in an .env configuration file and makes a test connection with the server to confirm their validity. Test call is handled by a Python API client generated from API definition. These credentials will be later used to communicate with the server.

Main script starts automatically with the device and terminates if credentials are not set up. Its modules are as follows:

* Frame ingestor: stateless service, which collects frames from Camera device, USB webcam or input video file
* Image analysis: performs image analysis to count people coming in and out. Prediction system uses OpenCV for people detection.
* Data batcher: Batches datapoints in preset intervals, saves batches to device memory
* API client: automatically generated API client which uploads batches of events
