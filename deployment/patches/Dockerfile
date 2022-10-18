FROM maven:3.8.4-jdk-11-slim AS build

COPY ./pom.xml /pom.xml
COPY ./dl4se-crawler /dl4se-crawler
COPY ./dl4se-model /dl4se-model
COPY ./dl4se-patches /dl4se-patches
COPY ./dl4se-server /dl4se-server
COPY ./dl4se-src2abs /dl4se-src2abs

RUN mvn -e --no-transfer-progress clean package -pl dl4se-patches -am -DskipTests

FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine

COPY --from=build /dl4se-patches/target/dl4se-patches-*.jar /patches.jar

ENTRYPOINT java \
-DDATABASE_HOST="$DATABASE_HOST" \
-DDATABASE_NAME="$DATABASE_NAME" \
-DDATABASE_PASS="$DATABASE_PASS" \
-DDATABASE_PORT="$DATABASE_PORT" \
-DDATABASE_USER="$DATABASE_USER" \
-Dfile.encoding=UTF-8 \
-jar patches.jar