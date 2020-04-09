# Architecture specification

There is a single server app that manages the database data and admins panel. It servers separated API endpoints for camera based devices and client Android devices to handle all the data and serveneeded data for clients.

**TODO:** System overview and how camera communicate with server

### Server and Android app communication
Communication between server and android app is handled via RESTful API which is built based on server data model and served as the public api.yaml file with the whole public api specification.

With the given API specification the [OpenAPI generator](https://github.com/OpenAPITools/openapi-generator) is used to generate the API handlers. (The generated classes are not included in the repository.)

App loads the data from server in the background asking only for data needed to rpesent on current app screen. The data is refreshed on every screen change in app.

When the connection with internet is lost by the device, app is suspended and waiting screen shows while waiting for active internet connection. The possible connections type supported by app are WiFi, cellular data and VPN connections.

**TODO**: write about user sending data to server from client app

## Management panel architecture

## API endpoint architecture 

**TODO:** Should it be as the separate paragraph in doc?

## Mobile client architecture

Client app designed for Android devices is developed in Android Studio in Kotlin and Java. Build process is managed by the Gradle build system. App uses [AndroidX support libraries](https://developer.android.com/jetpack/androidx) to handle android system libraries.

For concurrenrcy purpose the [RxKotlin](https://github.com/ReactiveX/RxKotlin) as the bindings to [RxJava](https://github.com/ReactiveX/RxJava) with [Android extensions](https://github.com/ReactiveX/RxAndroid) is used in project alongsite the standard [Kotlin coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html). In the feature all the asynchronous tasks are to be migrated to couroutines code. 

The app uses the OpenStreet Maps with the MapView designed in [osmdroid library](https://github.com/osmdroid/osmdroid) allowing the developers to customize the map look and easily manage the map data caching.

User interface needs also some data visualizations of all the data so the [data2viz library](https://github.com/data2viz/data2viz) is used to build all the needed plots and graphs from scratch in a Kotlin code and easily place them into the standard Android layouts.

For the RESTful API endpoints generation in Android app the [Kortlin code generator](https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/kotlin.md) is used.

**TODO**: write about handling the diffenrsnt languages in app, maybe somthing else




## Camera devices
