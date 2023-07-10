FROM maven:3.9.3-eclipse-temurin-11-alpine AS build

COPY ./pom.xml /pom.xml
COPY ./dl4se-analyzer /dl4se-analyzer
COPY ./dl4se-crawler /dl4se-crawler
COPY ./dl4se-model /dl4se-model
COPY ./dl4se-server /dl4se-server
COPY ./dl4se-src2abs /dl4se-src2abs

RUN mvn -e \
        --no-transfer-progress \
        clean \
        package \
        -pl dl4se-crawler \
        -am -DskipTests

FROM eclipse-temurin:11.0.19_7-jre-alpine

COPY --from=build /dl4se-crawler/target/dl4se-crawler-*.jar /crawler.jar

RUN apk update --quiet && \
    apk add --no-cache \
            --quiet \
            git \
            libstdc++ \
            openssl

RUN openssl s_client -showcerts -connect seart-ghs.si.usi.ch:443 </dev/null 2>/dev/null | \
    openssl x509 -outform PEM > /tmp/seart-ghs.si.usi.ch.pem

RUN keytool \
    -import \
    -alias seart-ghs.si.usi.ch \
    -file /tmp/seart-ghs.si.usi.ch.pem \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit \
    -noprompt

ENTRYPOINT java -jar crawler.jar