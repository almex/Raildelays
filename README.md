# Raildelays

Raildelays is a multi-module application comparable to [iRail project](http://project.irail.be/).
Main goal of this application is to collect all train delays from Belgium train company called [SNCB](http://www.belgianrail.be/fr)/[NMBS](http://www.belgianrail.be/nl).
It was developed with extensibility in mind and is able to deal with other vehicule delays (i.e: bus, airplane, truck,
cars,...).

## Comparing to iRail

If you talk about position and time of any vehicule traject you can view those data under multiple aspects :
* Timetable
* Liveboard
* Route log

iRail can query a timetable per train and per date or a liveboard per station. In contrario, Raildelays can query 
the route log of a train.

### Timetable (futur)
When we talk about train timetable or scheduling we only talk about what is the planed route for a train.
Meaning that a certain train will move from one station to another and the timetable show you the expected arrival 
time and the exepected departure time for each stop.

### Liveboard (present)
We use a liveboard to know directly if your train is on time or not. It gives you the expected time and the delay 
comparing to what is expected. It only show trains that are not arrived yet to a certain station.

### Route log (past)
Give you the expected time, the effective time and if a stop was deserved or not.

In the partical of SNCB/NMBS train, you should be able to know the timetable of a train based on its collected 
route log because timetables remains commonly the same for every day of a week and are subject to unfrequent changes.
But, you should consider all of those views as seperate source of data. You can have change of a timetable
leading to historying a timetable and then a route log should used as expected time the last version of the timetable.
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

## API
* repository layer
* service layer

## Front-end
* non-interactive batch engine (mobile or desktop)
* mobile UI (Android) <not implemented yet>
* desktop UI (Java FX) <not implemented yet>

# Application Architecture

To be able to analyze data from an external system (e.g. : www.railtime.be), we have to persiste them in our 
data model.

## Langages

* Java: choosen for its portability and its power.
* Groovy: choosen for its easyness to parse DOM documents (XML, XHTML,...).

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
