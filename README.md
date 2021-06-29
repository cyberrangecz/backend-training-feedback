# Web Application for Automated Feedback to Participants of Cybersecurity Training (backend part)

This project represents back-end part of application.

Author: Jan Demcak

Advisor: RNDr. Valdemar Svabensky

## Content
1. Requirements
2. Installation of the Project
3. Project Configuration
4. Running up the Project
5. Project modules

### Requirements:
Install all of the following technologies:

Technology     | Version      | URL to Download
-------------- | ------------ | ------------
Java (OpenJDK) | 11           | https://openjdk.java.net/
Maven          | 3.6.2        | https://maven.apache.org/

For OpenJDK and Maven set `system environment variables`: `JAVA_HOME` and `MAVEN_HOME` so that it will be possible to enter commands in command line (cmd).

For proper run of application some data provider is needed. For this, solution from KYPO team could be used.
Below listed repositories will be needed. For access please contact someone from KYPO development team, primary Pavel Seda (441048).

1. elk-portal-commands-events - provide docker-compose file for running ELK stack and script for filling up the database.
2. kypo-elasticsearch-service - microservice that connects into elastic and provide data from it. Branch with disabled security is needed.
3. data - repository with data from old trainings and description of data format.
4. kypo-elasticsearch-documents - build this project locally it is dependency needed for building kypo-elasticsearch-service

### Installation of the Project:
Open cmd in newly created directory `automated-feedback` and execute the command:
```
$ mvn clean install
```

After that, it should show you that all the modules of this project were successfully installed.
```
[INFO] automated-feedback ................................. SUCCESS [  0.219 s]
[INFO] feedback-persistence ............................... SUCCESS [ 37.808 s]
[INFO] feedback-api ....................................... SUCCESS [ 43.722 s]
[INFO] feedback-service ................................... SUCCESS [ 17.985 s]
[INFO] feedback-rest ...................................... SUCCESS [ 25.128 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```

For installation of projects in elk-portal-commands-events and kypo-elasticsearch-service use attached README files in these repositories.

### Project Configuration:
Configuration file `application.properties` is stored in feedback-rest module. 
These 3 training-specific attributes must be set for proper loading of commands, events and definition file:
1. client.url.definitions.definition.id
2. client.url.events.instance.id
3. client.url.commands.pool.id

### Running up the Project:
To run up the project you have to go to `feedback-rest` module. Open cmd and execute the following command:
```
$ mvn spring-boot:run
```
Then the system will start the project. You can access the REST API with Swagger UI on address: `http://localhost:8089/kypo-automation-feedback-service/api/v1/swagger-ui.html`.
After start, reference graph should be created manually by calling `POST` request: `http://localhost:8089/kypo-automation-feedback-service/api/v1/graphs/reference-graph`.
### Project modules:
Project is divided into several modules as:
* `feedback-rest`
  * Provides REST layer for communication with front-end.
  * Based on HTTP REST.
  * Documented with Swagger.
* `feedback-api`
  * Contains API (DTO classes)
    * Annotations for Swagger documentation are included.
  * Contains converters for DateTime processing.
  * Contains exceptions.
* `feedback-service`
    * Provides business logic of the application:
      * Map Entities to DTO classes and vice versa with MapStruct framework.
      * Calls other microservices which provide training data.
      * Load and process syntax and semantic files which are used to mistake analysis.
      * Calls persistence layer for database queries and combining the results as necessary.
      * Do mistake analysis and compute trainee graphs, reference graph and summary graph.
* `feedback-persistence`
  * Provides data layer of the application (Database queries).
  * Uses Spring Data JPA (Spring wrapper layer over JPA implemented with Hibernate framework).
  * Communicates with in-build H2 database.

* `automated-feedback`
  * The main project (parent maven project with packaging pom).
  * Contains configurations for all modules as dependency versions, dependency for spring boot parent project etc.
