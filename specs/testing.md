# Testing specification

Version number: 1.1

Authors: Krzysztof Antoniak, Robert Michna, Maciej Procyk

### Changelog

* v1.0 (2020-05-04): Initial revision
* v1.1 (2020-05-05): Spelling and wording improvements

### Overview

This document provides testing strategy for all system components. Functional
and non-functional tests are broken down by type in separate sections. Each
component checks can be performed separately, except for integration testing.

## Functional tests

### Unit testing

#### Django server

Django [unit tests](https://docs.djangoproject.com/en/3.0/topics/testing/overview/) allow us
to test many aspects of the server:
* Database tests, assuring that the database follows the designed model, works
  well under load and is properly configured. Database tests execute some
  rollback emulation testing.
* Regular unit tests for testing the server app against regressions.

#### Android application

Android application test suite comprises regular unit tests running on a
developer workstation and the instrumented tests which run on an Android device
(or its emulator in automated environment).

* **Unit tests** are compiled to bytecode and run on JVM. They should test the
  logic of every individual part of Android app in order to easily catch
  software regressions introduced by new changes to code. The
  [Roboelectric](http://robolectric.org/) library is designed to emulate
  Android framework part in such tests and should be used in test writing
  process.
* **Instrumented tests** are to be run on Android emulator because they need
  access to real device resources. They are useful in testing functionality
  which cannot be easily mocked by frameworks. Good example is a test that
  validates a good implementation of *Parcelable* interface.

These types of tests are to be defined as a part of Android project in separate
directories and can be run on single developer workstation. There should be also
definitions of testing parts in GitHub workflows that builds the debug and
release versions of application. They would run as Gradle tasks in a
pre-defined process which publish artifacts to GitHub store when successfully
completed.

#### Camera devices

Parts of the camera code can be run and tested in any environment. This part is
covered by `pytest` unit tests. These tests can be run twofold:

* **Local environment**: Call `pytest /camera/rpi-files/` or
  `./camera/rpi-files/format-lint-test.sh` for all checks.
* **Automated GitHub workflow**: All tests are run automatically when you try to
  merge code into `master` branch. If any test fails, GitHub prevents PR
  merging. For implementation details, see `camera_cq` workflow
  [here](https://github.com/pooh-labs/eaterslab/blob/master/.github/workflows/camera_cq.yaml).

Functionality not covered by unit tests (like accessing device I/O channels) is covered by other tests.

### Integration testing

Integration tests allow to test the work of groups of units. They should test
the interactions between integrated components or even parts of the system.
 
#### Android application

In android projects integration tests should validate the app's behavior from
the module level. They can test:
* the cached repository layer that interacts with external sources of data as
  well as caches some data internally
* interactions between Views and ViewModels by validating the
  layout XML

To carry out this type of tests the Espresso Intents library can be used. It
allows to validate intents sent by the application under tests. It's like the
standard Mockito library but created specially for Android Intents.

These tests are also to be run as the repository workflow that validates as the
second step of Android build process the ability of the parts to be released.

### Smoke testing

#### Django server

The standard framework
[django-smoke-tests](https://pypi.org/project/django-smoke-tests/) can be used in
this part of tests to easily integrate them with Django system.

Smoke tests can be run by the developers or testers to check against any
possible unwanted outcomes from HTTP requests. This app identifies all possible
endpoints by looking into Django URL files, runs the HTTP requests and reports
any unexpected responses. These tests enhance security of our server app.

#### Camera devices

Device smoke testing is a half manual process. Device must be configured before
the test.

1. Tester turns on the device.
2. After defined time (e.g. 60 seconds) tester checks the admin panel to
   confirm that device status in the panel is `on`.

Please note these steps are also part of the installability test.

### Acceptance/Beta testing

#### Android application

Android application should be tested manually before releasing. All the
business requirements are checked during this testing phrase.

These tests should check if none of the unknown errors are thrown to user
interface as exceptions. They should also check if app is designed in
accordance with Material Design guidelines to make it easy to use essential app
features with ease.

After the app crashes Logcat logs are available in Android devices with the url
of the exception stacktrace. Thanks to this part Android environment the app
doesn't have to be run plugged to computer with Debugging enabled because all
the needed data of exception reason can be obtained with described system of
crash reports.

These tests can give the feedback to UI designers of the application about some
unserviceable parts of the app that in most of the cases can be easily fixed but
the feedback from users is needed.

#### Django server

Django provides a set of tools that come in handy when writing tests. Using
every specified part of the model on the server is checked by writing standard
requests to server run locally as well as on the development environment.

These test would check if the API definitions are available on server and if
requests to some basic API objects are done correctly. Special set of data is
prepared to be loaded on such testing environment using the JSON file which
contains some human readable data for the database. Thanks to this mechanism the
received data from the API endpoint can be compared with the specified in this
JSON file.

##### Automated Selenium use-case testing

Selenium allows for recording website-user interactions for later use in form of
testing. Selenium offers a quick way to check against any problems related to
user interaction with the server, giving us the perfect way to test the admin
part of the django app.

### Usability testing

#### Android application

Usability tests should be done by external testers not necessarily familiar with
technologies used in project to get the feedback on the app usability in basic
use-cases. They should check if every part of the defined user flows is usable
for standard users and if there are no unintuitive parts in user navigation
through the app interface which could cause them getting lost.

## Non-functional tests

### Performance testing

#### Django server

Performance tests can be simulated by issuing multiple request to the server at
the same time via camera devices. As for now, no further testing is required.

In the future the tests done by automated environments can be done. In order to
do so the mocked clients of app can be written to simulate the usage of the
system by many clients. In this way most of the bottlenecks at the server side
can by found by checking the server performance manually when mocked tests are
evaluated.

### Stress testing

#### Django server

Stress tests can be performed in similar manner as the performance tests but
with higher load.

### Maintainability testing

#### Camera devices

**Code quality checks**

Python code can be checked to `master` branch only if it adheres to Wemake
Python Styleguide. Wemake is an overset of
[PEP8](https://www.python.org/dev/peps/pep-0008/) coding standard. For
implementation details, see `camera_cq` workflow definition
[here](https://github.com/pooh-labs/eaterslab/blob/master/.github/workflows/camera_cq.yaml).

### Installability testing

#### Django server

![Webserver deploy dev](https://github.com/pooh-labs/eaterslab/workflows/Webserver%20deploy%20dev/badge.svg)
![Webserver deploy for production](https://github.com/pooh-labs/eaterslab/workflows/Webserver%20deploy%20for%20production/badge.svg)

Django server app is deployed to production as well as the development
environments which are located at the external servers. There are workflows
which deploy the application from `master` branch to production server and any
other branch to development environment.

The workflow is realized using single API key from server which allows to deploy
the whole application using standard bash commands. After deploying phase
servers are pinged by the workflow if they are set up correctly.  

#### Android application

![Build Android APK debug file](https://github.com/pooh-labs/eaterslab/workflows/Build%20Android%20APK%20debug%20file/badge.svg)
![Build Android APK release unsigned file](https://github.com/pooh-labs/eaterslab/workflows/Build%20Android%20APK%20release%20unsigned%20file/badge.svg)

Android application deployment is build as a GitHub workflow which builds the
release and debug versions of application using Gradle *assemble* and
*assembleDebug* tasks in a special Docker image.

At the end of the process app is sent to the production (after release) and
development (after any successful build) server and is tested on different
devices of project members by just downloading the standard APK files from the
server. The app is tested on devices differing in size and specification to
gain certainty that it will run on most of the clients devices.

#### Camera devices

Camera installability test is a half-manual process. For the test to pass, all
steps below must execute on any device matching specifications described in
architecture specification.

1. Installation script reads user input:
    * device name,
    * device key,
    * Wifi network name,
    * Wifi network password,
    * API endpoint address.
2. Installation script generated card image file (`.img`).
3. Tester writes image file to an SD card.
4. Tester turns the device on.
5. After defined time (e.g. 60 seconds) tester checks admin panel to confirm
   that device status in the panel is `on`.

Installation script is a planned feature, see
[#91](https://github.com/pooh-labs/eaterslab/issues/91) for progress.

### Compliance testing

**OpenAPI 2.0 schema checker**: 

![API checks](https://github.com/pooh-labs/eaterslab/workflows/API%20checks/badge.svg)

API definition file, `/specs/api.yaml`, must conform to OpenAPI 2.0 schema.
Workflow
[`api_checks`](https://github.com/pooh-labs/eaterslab/blob/master/.github/workflows/api_checks.yaml)
enforces this requirement for each PR to branch `master`.
