# Testing specification

This document provides testing strategy for all system components. Functional and non-functional tests are broken down by type in separate sections. Each component checks can be performed separately, except for integration testing.

## Functional tests

### Unit testing

Django server: [unit testing](https://docs.djangoproject.com/en/3.0/topics/testing/overview/)?

Android application: [unit testing](https://developer.android.com/training/testing/unit-testing)?

Camera device code uses `pytest` for unit testing. All unit tests must pass to complete any pull request to master branch. (To be done, see [#90](https://github.com/pooh-labs/eaterslab/issues/90) for progress).

### Integration testing

#### Android application

#### Django server

### Smoke testing

Django server: [this package](https://pypi.org/project/django-smoke-tests/)?

#### Camera devices

Device smoke testing is a half manual process. Device must be configured before the test.

1. Tester turns on the device.
2. After defined time (e.g. 60 seconds) tester checks admin panel to confirm that device status in the panel is `on`.

Please note these steps are also part of the installability test.

### Acceptance/Beta testing

#### Android application

* Automated [Espresso](https://developer.android.com/training/testing/ui-testing/espresso-testing)/Appium use-case testing?

#### Django server

* Automated Selenium use-case testing?

### Black-box testing


### Usability testing

#### Android application


## Non-functional tests

### Performance testing

#### Django server

#### Camera devices?

### Stress testing

#### Django server

### Maintainability testing

#### Camera device

Python code can be checked to `master` branch only if it adheres to Wemake Python Styleguide. Wemake is an overset of [PEP8](https://www.python.org/dev/peps/pep-0008/) coding standard. For implementation details, see `camera_cq` workflow definition [here](https://github.com/pooh-labs/eaterslab/blob/master/.github/workflows/camera_cq.yaml).

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

API definition file, `/specs/api.yaml`, must conform to OpenAPI 2.0 schema. Workflow [`api_checks`](https://github.com/pooh-labs/eaterslab/blob/master/.github/workflows/api_checks.yaml) enforces this requirement for each PR to branch `master`.
