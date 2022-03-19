FROM maven:3.8.4-jdk-11-slim AS build

COPY ../../pom.xml /pom.xml
COPY ../../dl4se-crawler /dl4se-crawler
COPY ../../dl4se-model /dl4se-model

RUN mvn -e -f pom.xml clean package -Dmaven.test.skip

FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine

COPY --from=build /dl4se-crawler/target/dl4se-crawler-*.jar /crawler.jar

RUN apk update && apk add git

COPY ../../deployment/scripts/wait-service-exit.sh /wait.sh
RUN chmod +x /wait.sh

ENTRYPOINT /wait.sh $LIQUIBASE_HOST && java \
    -DDATABASE_HOST=$DATABASE_HOST \
    -DDATABASE_NAME=$DATABASE_NAME \
    -DDATABASE_PASS=$DATABASE_PASS \
    -DDATABASE_PORT=$DATABASE_PORT \
    -DDATABASE_USER=$DATABASE_USER \
    -Dfile.encoding=UTF-8 \
    -jar crawler.jar