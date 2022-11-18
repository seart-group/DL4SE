FROM maven:3.8.4-jdk-11-slim AS build

COPY ./pom.xml /pom.xml
COPY ./dl4se-crawler /dl4se-crawler
COPY ./dl4se-model /dl4se-model
COPY ./dl4se-server /dl4se-server
COPY ./dl4se-src2abs /dl4se-src2abs

RUN mvn -e --no-transfer-progress clean package -pl dl4se-crawler -am -DskipTests

FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine

COPY --from=build /dl4se-crawler/target/dl4se-crawler-*.jar /crawler.jar

COPY ./deployment/crawler/entrypoint.sh ./
RUN chmod 777 ./entrypoint.sh

RUN apk update && apk add git

ENTRYPOINT ["/entrypoint.sh"]