FROM maven:3.9.3-eclipse-temurin-11-alpine AS build

COPY ./pom.xml /pom.xml
COPY ./dl4se-analyzer /dl4se-analyzer
COPY ./dl4se-crawler /dl4se-crawler
COPY ./dl4se-model /dl4se-model
COPY ./dl4se-server /dl4se-server

RUN mvn -e \
        --no-transfer-progress \
        clean \
        package \
        -pl dl4se-server \
        -am -DskipTests

FROM eclipse-temurin:11.0.20_8-jre-alpine

COPY --from=build /dl4se-server/target/dl4se-server-*.jar /server.jar

RUN apk update --quiet && \
    apk add --no-cache \
            --quiet \
            git \
            libstdc++

ENTRYPOINT java -jar server.jar
