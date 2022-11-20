# Docker

Although we have previously covered how to run each platform component on its own, there may be instances where you
want to test the platform in an isolated environment. Since each DL4SE component equates to a container, we make use of
Docker Compose. Our platform is compartmentalized into five services:

- `dl4se-db`: Responsible for hosting our platform database;
- `dl4se-liquibase`: Used to automatically perform database migrations on startup;
- `dl4se-crawler`: Host to the crawler application;
- `dl4se-server`: Host to the back-end server;
- `dl4se-website`: Responsible for distributing the website resources to clients.

## Setup

Dockerizing the platform requires first creating the `.env` file that `docker-compose` will use to create and start all
the defined services. This file essentially contains the environment variables that we have defined beforehand. Although
you could manually create this file with all the settings, if you have set up the environment variables, then you can
quickly generate this file using the [`deployment/envgen.sh`](deployment/envgen.sh) script. The final file should look
something like this:

```dotenv
COMPOSE_PROJECT_NAME=dl4se

DATABASE_NAME=dl4se
DATABASE_USER=dl4se_admin
DATABASE_PASS=Lugano2022
DATABASE_PORT=5432

# ...
```

## Deployment

After that, creating the containers and starting all the services is as simple as running the following:

```shell
docker-compose -f deployment/docker-compose.yml up -d
```

Shutting down the services and removing the created containers:

```shell
docker-compose -f deployment/docker-compose.yml down
```