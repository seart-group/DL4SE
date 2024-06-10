# Local Development

## Crawler

Compile the crawler module by running:

```shell
mvn package -pl dl4se-crawler -am
```

To run the compiled JAR file directly:

```shell
java \
  -DDATABASE_HOST="$DATABASE_HOST" \
  -DDATABASE_NAME="$DATABASE_NAME" \
  -DDATABASE_PASS="$DATABASE_PASS" \
  -DDATABASE_PORT="$DATABASE_PORT" \
  -DDATABASE_USER="$DATABASE_USER" \
  -DCODE_SEARCH_URL=http://gym.si.usi.ch:48001/api/r/search \
  -Dfile.encoding=UTF-8 \
  -jar dl4se-crawler/target/dl4se-crawler-1.0.0.jar
```

## Server

Compile the module into a JAR:

```shell
mvn package -pl dl4se-server -am
```

And run the archive directly:

```shell
java -Dfile.encoding=UTF-8 -jar dl4se-server/target/dl4se-server-1.0.0.jar
```

## Website

First install all the dependencies by running:

```shell
npm install
```

To test a development build of the application locally:

```shell
npm run serve
```
