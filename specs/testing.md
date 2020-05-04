# Testing specification

This document provides testing strategy for all system components. Functional and non-functional tests are broken down by type in separate sections. Each component checks can be performed separately, except for integration testing.

## Functional tests

### Unit testing

#### Django server
Django server: [unit testing](https://docs.djangoproject.com/en/3.0/topics/testing/overview/).

Django unit tests allow us to test many aspects of the server:
* Database tests, assuring that the database follows the designed model, works well under load and is properly configured. Also some rollback emulation testing
* Normal unit tests for testing the server app against code bugs.

#### Android application
Android application can be tested using local tests running on developer computer as well as the instrumented tests which run on Android device (or its emulator in automated environment).
* **Local tests** are compiled to bytecode and run on JVM. They should test the logic of every individual part of Android app in order to easily catch software regressions introduced by new changes to code.
 The [Roboelectric](http://robolectric.org/) library is designed to emulate Android framework part in such tests and should be used in tests writing process.
*  **Instrumented tests** are to be run on Android emulator  because they need access ro real device resources. They are useful in testing functionalities which cannot be easily mocked by frameworks. Good example is a test that validates a good implementation of *Parcelable* interface.

These types of tests are to be defined as a part of Android project in separate directories and can be run on single developer workstation. There should be also definitions of testing parts in github workflows that makes the releases of debug and offical versions of application.
They would run as the Gradle tasks as a pre-build process that give an access to release part (when successfully completed).

#### Camera devices

Camera device code is covered by `pytest` unit tests. These tests can be run twofold:

* **Local environment**: Call `pytest /camera/rpi-files/` or `./camera/rpi-files/format-lint-test.sh` for all checks.
* **Automated GitHub workflow**: All tests are run automatically when you try to merge code into `master` branch. If any test fails, GitHub prevents PR merging. For implementation details, see `camera_cq` workflow [here](https://github.com/pooh-labs/eaterslab/blob/master/.github/workflows/camera_cq.yaml).

### Integration testing
The  integration tests in Android development process are 

#### Android application

#### Django server

### Smoke testing

Django server: [django-smoke-tests](https://pypi.org/project/django-smoke-tests/).

Smoke tests can be run by the user to check against any possible unwanted outcomes from http requests. 
This app identifies all possible endpoints by looking into django url files, runs the http requests and reports any unexpected responses. These tests enchance security of our server app.

#### Camera devices

Device smoke testing is a half manual process. Device must be configured before the test.

1. Tester turns on the device.
2. After defined time (e.g. 60 seconds) tester checks admin panel to confirm that device status in the panel is `on`.

Please note these steps are also part of the installability test.

### Acceptance/Beta testing

#### Android application

* Automated [Espresso](https://developer.android.com/training/testing/ui-testing/espresso-testing)/Appium use-case testing?

#### Django server

* Automated Selenium use-case testing.

Selenium allows for recording website-user interactions for later use in form of testing.
Selenium offers a quick way to check against any problems related to user interaction with the server, giving us the perfect way to test the admin part of the django app.

### Black-box testing


### Usability testing

#### Android application


## Non-functional tests

### Performance testing

#### Django server

In addition to manual testing, running unit tests for both server and camera devices gives enough insight into performance of the server app.

### Stress testing

#### Django server

Stress tests can be simulated by running multiple request to the server at the same time via camera devices. As for now, no further testing is required.

### Maintainability testing

#### Camera devices

* **Code quality checks**: Python code can be checked to `master` branch only if it adheres to Wemake Python Styleguide. Wemake is an overset of [PEP8](https://www.python.org/dev/peps/pep-0008/) coding standard. For implementation details, see `camera_cq` workflow definition [here](https://github.com/pooh-labs/eaterslab/blob/master/.github/workflows/camera_cq.yaml).

### Installability testing

#### Django server

* Automated pushes to Heroku with DB setup? We need to confirm that website is up and running after the push. Can it be done from workflow (e.g. `wget` with HTTP code)? 

#### Android application

* Automated APK builds?

#### Camera devices

Camera installability test is a half-manual process. For the test to pass, all steps below must execute on any device matching specifications described in architecture specification.

1. Installation script reads user input:
    * device name,
    * device key,
    * Wifi network name,
    * Wifi network password,
    * API endpoint address.
2. Installation script generated card image file (`.img`).
3. Tester writes image file to an SD card.
4. Tester turns the device on.
5. After defined time (e.g. 60 seconds) tester checks admin panel to confirm that device status in the panel is `on`.

Installation script is a planned feature, see [#91](https://github.com/pooh-labs/eaterslab/issues/91) for progress.

### Compliance testing

* **OpenAPI 2.0 schema checker**: API definition file, `/specs/api.yaml`, must conform to OpenAPI 2.0 schema. Workflow [`api_checks`](https://github.com/pooh-labs/eaterslab/blob/master/.github/workflows/api_checks.yaml) enforces this requirement for each PR to branch `master`.
