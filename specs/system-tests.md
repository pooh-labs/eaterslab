# System tests specification

This document provides testing strategy for all system components. Functional and non-functional tests are broken down by type in separate sections. Each component checks can be performed separately, except for integration testing.

## Functional tests

### Unit testing

Django server: [unit testing](https://docs.djangoproject.com/en/3.0/topics/testing/overview/)?

Android application: [unit testing](https://developer.android.com/training/testing/unit-testing)?

Camera devices: `pytest` unit testing?

### Integration testing

#### Android application

#### Django server

### Smoke testing

Django server

Camera device: (TODO) start up, connect to server, power down. Check that server received events.

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

Camera devices: GH workflow (`wemake-python-styleguide`) for testing PEP8 coding standard conformance

### Installability testing

#### Django server

* Automated pushes to Heroku with DB setup? We need to confirm that website is up and running after the push. Can it be done from workflow (e.g. `wget` with HTTP code)? 

#### Android application

* Automated APK builds?

#### Camera devices

* Automated generation of .img card images (TBD).

### Compliance testing

* OpenAPI conformance testing
