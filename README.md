# Raildelays 
[![Build Status](https://travis-ci.org/almex/Raildelays.svg)](https://travis-ci.org/almex/Raildelays) [![Coverage Status](https://coveralls.io/repos/almex/Raildelays/badge.svg?branch=master&service=github)](https://coveralls.io/github/almex/Raildelays?branch=master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.almex/raildelays-assembly-descriptor/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.almex/raildelays-assembly-descriptor)

Raildelays is mainly a Java application which accumulates train delays from the Belgian railway company called [NMBS](http://www.belgianrail.be/nl)/[SNCB](http://www.belgianrail.be/fr).
In futur development, we will extend possibilities to be able to deal with other railway company or other vehicles
 (i.e: bus, airplane, truck, cars,...).

### Timetable (futur)
When we talk about train timetable or scheduling we only talk about what is the planed route for a train.
Meaning that a certain train will move from one station to another and the timetable show you the expectedTime arrival
time and the exepected departure time for each stop.

### Liveboard (present)
We use a liveboard to know directly if your train is on time or not. It gives you the expectedTime time and the delay
comparing to what is expectedTime. It only show trains which are not arrived yet to a certain station.

### Route log (past)
Give you the expectedTime time, the effective time and if a stop was deserved or not.

In practical with NMBS/SNCB trains, you should be able to know the timetable of a train based on its collected 
route log because timetables remain commonly the same for every day of a week and are subject to unfrequent changes.
But, you should consider all of those views as seperate source of data. You can have change of a timetable
leading to different versions and then a route log should used, as expectedTime time, the last version of the timetable.
A liveboard is a little in between but it gives you information only for a certain station and you cannot follow
the complete train traject.

# Building from Source

Clone the git repository using the URL on the Github home page:

    $ git clone git://github.com/almex/Raildelays.git
    $ cd Raildelays

## Command Line
Use Maven 3.0, then on the command line:

    $ mvn install

or, you can execute integration tests via:

    $ mvn install -Pit

# Application Design

## Input
* HTTP Client/Streamer
* HTTP Parser

## Output 
* Excel Sheet
* Flat file

## API
* repository layer
* service layer

## Front-end
* non-interactive batch engine (mobile or desktop)
* desktop UI (Java FX)
* mobile UI (Android) <not implemented yet>

# Application Architecture

To be able to analyze data from an external system (e.g. : www.railtime.be), we have to persist them in our 
data model.

## Languages

* Java: chosen for its portability and its power.
* Groovy: chosen for its easiness to parse DOM documents (XML, XHTML,...).

## Frameworks

* Apache POI: to write Microsoft Excel sheets
* Spring Core: for Dependency Injections and Invertion of Control to make the leverage the modularity 
of the application to a high level.
* Spring Batch: to build a non-interactive and fully automated batch process.
* Spring Data: to simplify DAO creation.
* JPA: to abstract persistence layer.
* Hibernate: as a JPA implementation.
* JUnit: as a testing framework.
* Spring Test: as an add-on to JUnit to simply testing.
* Derby: as an embedded database (Desktop).
* HSQLDB: as an in-memory database to execute integration tests (default with Spring Test)


# Quality Assurance

To able to measure Code Quality, the application was configured to be used with [Sonar](www.sonasource.org).

The JaCoCo Maven plug-in has been configured to measure code coverage separately for unit tests and integration tests.

# Licensing

This project is licensed under the terms of the MIT license.
