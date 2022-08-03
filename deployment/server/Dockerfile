FROM maven:3.8.4-jdk-11-slim AS build

COPY ./pom.xml /pom.xml
COPY ./dl4se-crawler /dl4se-crawler
COPY ./dl4se-model /dl4se-model
COPY ./dl4se-server /dl4se-server
COPY ./dl4se-src2abs /dl4se-src2abs

RUN mvn install:install-file \
    -Dfile="./dl4se-src2abs/lib/javalexer.jar" \
    -DgroupId="edu.wm.cs" \
    -DartifactId="javalexer" \
    -Dversion="1" \
    -Dpackaging="jar"

RUN mvn -e -f pom.xml clean package -pl dl4se-server -am -DskipTests

FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine

COPY --from=build /dl4se-server/target/dl4se-server-*.jar /server.jar

ENTRYPOINT java -jar server.jar