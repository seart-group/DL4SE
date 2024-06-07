FROM maven:3.9.7-eclipse-temurin-11-alpine AS build

COPY ./pom.xml /pom.xml
COPY ./dl4se-model /dl4se-model
COPY ./dl4se-server /dl4se-server
COPY ./dl4se-spring /dl4se-spring
COPY ./dl4se-transformer /dl4se-transformer

RUN mvn -e \
        --no-transfer-progress \
        package \
        -Pserver \
        -am -DskipTests

FROM eclipse-temurin:11.0.23_9-jre-alpine

COPY --from=build /dl4se-server/target/dl4se-server-*.jar /server.jar

RUN apk update --quiet && \
    apk add --no-cache \
            --quiet \
            curl \
            git \
            libstdc++

ENTRYPOINT java $JAVA_OPTS -jar server.jar

EXPOSE 8080

HEALTHCHECK \
    --start-period=30s \
    --interval=60s \
    --timeout=5s \
    --retries=5 \
    CMD curl --silent --fail --request GET http://localhost:8080/api || exit 1
