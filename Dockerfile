FROM maven:3.6.2-jdk-11-slim AS build

## default environment variables for database settings
ARG USERNAME=postgres
ARG PASSWORD=postgres
ARG POSTGRES_DB=kypo-training-feedback
## rename the artifact id to something else, e.g. kypo-training-feedback
ARG PROJECT_ARTIFACT_ID=kypo-training-feedback

## default link to proprietary repository, e.g., Gitlab repository
ARG PROPRIETARY_REPO_URL=YOUR-PATH-TO-PROPRIETARY_REPO

# install
RUN apt-get update && apt-get install -y supervisor postgresql rsyslog netcat

# configure supervisor
RUN mkdir -p /var/log/supervisor

# configure postgres
RUN /etc/init.d/postgresql start && \
    su postgres -c "createdb -O \"$USERNAME\" $POSTGRES_DB" && \
    su postgres -c "psql -c \"ALTER USER $USERNAME PASSWORD '$PASSWORD';\"" && \
    /etc/init.d/postgresql stop

# copy only essential parts
COPY /etc/kypo-training-feedback.properties /app/etc/kypo-training-feedback.properties
COPY entrypoint.sh /app/entrypoint.sh
COPY supervisord.conf /app/supervisord.conf
COPY pom.xml /app/pom.xml
COPY src /app/src

WORKDIR /app

# build training feedback service
RUN mvn clean install -DskipTests -Dproprietary-repo-url=$PROPRIETARY_REPO_URL && \
    cp /app/target/$PROJECT_ARTIFACT_ID-*.jar /app/kypo-training-feedback.jar && \
    chmod a+x entrypoint.sh

EXPOSE 8082
ENTRYPOINT ["./entrypoint.sh"]
