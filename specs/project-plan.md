# Project plan

Version number: 1.0

Authors:
* Jakub Walendowski

### Changelog

* v1.0 (2020-05-19): Initial revision

## Introduction

### Purpose

This document describes the organization and work plan of the "EastersLab" project. The 
purpose of the document is to define all activities and procedures related to the implementation 
of the project.

### Scope

Document includes:
* Defining the tasks and responsibilities of each member of the project team.
* Organizational details, in particular:
	* Technical processes plans
	* Supervision and control of the project

### Rest of the document

The rest of the document contains:
* Project plan
* Description of the tasks of team members
* Project supervision and control plans

## Project overview

### Purpose and scope of the project

The aim of the project is to create a system supporting the work of canteens. The system's task is to 
inform customers about cafeteria state to saves their time and helps them decide whether to go for lunch 
at given time. They should know how many people are currently having lunch and what the menu is 
beforehands. With rating options, users can guide each other in search for the best dishes. Foreign 
visitors and disabled users can easily access all data on their devices.
Owners will benefit from analytics. Historical data and occupancy predictions help to prepare for lunch 
in advance, while updating customers mitigates traffic spikes. Publishing menus is a new marketing 
channel promoting cafe directly to interested users. Administration panel can automate posting to 
social media channels. Collected feedback will contribute to service improvements.

### Assumptions and dependencies

* Project is implemented by a team of four MIM students.
* Project is implemented as part of the subject "Software Engineering" during classes and the team's 
additional meetings.
* System is expected to be delivered by mid-June 2020.
* The cost of the entire project cannot exceed the amount of development grants awarded to the team, 
which are currently zero, so the project must be implemented
on a voluntary basis.

### Project products

* Admin panel
* Owner pannel
* Client app
* Database
* Camera device software

### Procedure for changes in the project plan

During the implementation of the project, changes may be made to this document. Changes must be 
important (minor corrections should not be applied to avoid unnecessary mess) and approved by the 
entire implementation team.

## Project organization

### Organizational structure

* Krzysztof Antoniak - camera devices and server
* Robert Michna - server
* Maciej Procyk - android application and server
* Jakub Walendowski - android application

### External contacts

The team contacts the project supervisor. He controls the state of work on the project, 
the substantive correctness of created documents and the purposefulness of the entire enterprise.

## Project management

### Estimates

The project should be completed in mid-June 2020. The project will be implemented with zero 
financial outlay, i.e. it will be financed from own resources of the team members. The project 
budget may be enlarged if the team receives a development grant.

### Project Schedule

The project should be realized in a few phases specified in table below.

| Project phase                            | date from  | date to  | comments  |
|------------------------------------------|------------|----------|-----------|
| Specified business requirements          | 03.03.2020 |10.03.2020|    -      |
| The choice of technology used in project | 03.03.2020 |10.03.2020|    -      |
| Modules implementation                   | 10.03.2020 |09.06.2020|    -      |
| Documentation writing                    | 10.03.2020 |02.06.2020|    -      |
| Tests writing                            | 28.04.2020 |02.06.2020|    -      |
| Beta version of system                   | 09.06.2020 |16.09.2020|    -      |

### Resources

Project is carried out by 4 students of Computer Science at the University of Warsaw. The project team members 
should have the specified skills in order to easily get introduced to project specification:
* base knowledge of Python, Kotlin and bash is required as the most of the code of the project will be written
in these languages
* base knowledge about neural networks models which are to be used on camera devices
* good understanding of databases working and modeling the project elements
* good understanding of concurrency problems in data management projects
* good communication skills as the project will be realizes remotely so with limited communication possibilities

### Employment plan

Students are going to realize the project specification in their free time as the part of the Software 
Engineering classes in the 2020. They won't get any money reward as the project should give them the 
ability to get introduced into project management and writing some bigger project in team that is bigger 
than two people.

### Training plan

Students are going to improve their programming skills in different languages during this project realization.

### Project supervision and control

Decisions on the next steps in the project are made every week or every two weeks on team meetings. Any change made by one of the team members must be approved by at least one other person.

## Technical processes

### Programming

**TODO(kantoniak)**

### Tools and technologies

Management and planning tools are shared between all projects for unified planning process. Subprojects use different tools to match technical requirements.

* `.git` as a control version system
* GitHub [issues](https://help.github.com/en/github/managing-your-work-on-github), [milestones](https://help.github.com/en/github/managing-your-work-on-github/tracking-the-progress-of-your-work-with-milestones) and [a project board](https://help.github.com/en/github/managing-your-work-on-github/managing-project-boards) for work organization
* GitHub [pull requests](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/about-pull-requests) and [workflows](https://help.github.com/en/actions/configuring-and-managing-workflows) for code reviews and testing
* Markdown files for documents and specifications
* [Visual Paradigm Online](https://online.visual-paradigm.com) for technical diagrams
* For server development:
  * **TODO (Rhantolqu): Please add versions and links**
  * Python X.X and Django X.X
    * Django Rest Framework for API endpoint
  * HTML 5, CSS 3, ECMAScript 2017 (JavaScript 8)
  * PostgreSQL X.X and SQLite 3 databases
* For Android development:
  * **TODO (avan1235): Please add versions and links**
  * Kotlin X.X
  * Android X.X SDK
  * Android Studio X.X
* For camera device development:
  * **TODO (kantoniak): Links, versions**
  * Raspbian Buster Lite February 2020 edition
  * Python 3.7
  * OpenCV 4.2.0
  * OpenJDK 11 for API client generation

### Infrastructure

In product development stage members use their own machines for development.
Development machines run on Linux system from Debian family. Members are free
to configure systems to best suit their development style.

Testing infrastructure depends on subproject type:
* All subprojects use GitHub platform running Docker containers for initial
  testing (linting, unit tests etc.)
* API endpoint and administration panels are shipped as a Heroku website
* Android application is tested in emulator or on a single device
* Camera software is tested in a VirtualBox machine

Customer-facing website is exposed as a separate Heroku project. Android
application will be shipped to Google Play store. Camera software is copied to
SD cards for customers' devices.

### Product acceptance plan

**TODO(kantoniak)**

https://sceweb.uhcl.edu/helm/RationalUnifiedProcess/process/artifact/ar_acpl.htm

### Change management plan

Each proposed change is discussed by the whole team. The effects of the change are considered and, if approved, the selected team member makes corrections to the documentation or code.

### Problem solving plan

Problems will be discussed at regular team meetings and solved in a democratic way after considering the possible advantages and disadvantages of the solution.


