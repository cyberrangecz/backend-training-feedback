# KYPO Training Feedback

The project represents back-end for automated feedback to participants of cybersecurity training. Application provides
data for the visualizations of various graphs in the KYPO CRP.

### Build and Start the Project Using Docker

#### Prerequisites

Install the following technology:

Technology       | URL to Download
---------------- | ------------
Docker           | https://docs.docker.com/install/

#### 1. Preparation of Configuration Files

To build and run the project in docker it is necessary to
prepare [training-feedback.properties](https://gitlab.ics.muni.cz/muni-kypo-crp/backend-java/kypo-training-feedback/-/blob/master/etc/kypo-training-feedback.properties)
file.

#### 2. Build Docker Image

In the project root folder (folder with Dockerfile), run the following command:

```shell
$ sudo docker build \
  --build-arg PROPRIETARY_REPO_URL=https://gitlab.ics.muni.cz/api/v4/projects/2358/packages/maven \
  -t kypo-training-feedback-image \
  .
```

Dockefile contains several default arguments:

* USERNAME=postgres - the name of the user to connect to the database.
* PASSWORD=postgres - user password.
* POSRGRES_DB=training - the name of the created database.
* PROJECT_ARTIFACT_ID=kypo-training-feedback - the name of the project artifact.
* PROPRIETARY_REPO_URL=YOUR-PATH-TO-PROPRIETARY_REPO.

Those arguments can be overwritten during the build of the image, by adding the following option for each argument:

```bash
--build-arg {name of argument}={value of argument} 
``` 

#### 3. Start the Project

Start the project by running docker container. Use the following command:

```shell
$  sudo docker run \
   --name kypo-training-feedback-container -it \
   --network host \
   -p 8083:8083 \
   kypo-training-feedback-image
```

Instead of using PostgreSQL database, you can use the in-memory database H2. It just depends on the provided
configuration. Add the following option to use the custom property file:

```shell
-v {path to your config file}:/app/etc/training.properties
```

To create a backup for your database add the following docker option:

```shell
-v db_data_training:/var/lib/postgresql/11/main/
```

Add the following environment variable to wait for other services until they are up and running:

```shell
-e SERVICE_PRECONDITION="localhost:8084, localhost:8082"
```  
