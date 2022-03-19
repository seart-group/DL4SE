# Docker Commands

## `docker`

To list the containers:
```shell
docker container ls -as
```

To list the images:
```shell
docker image ls -as
```

To list the volumes:
```shell
docker volume ls
```

To access the shell of a container:
```shell
docker exec -it CONTAINER_NAME /bin/sh
```

To display the container log:
```shell
docker logs CONTAINER_NAME -f
```

To stop a container:
```shell
docker stop CONTAINER_NAME
```

To completely remove all data:
```shell
docker system prune -af --volumes
```

## `docker-compose`

To preview the final compose setup:
```shell
docker-compose -f deployment/docker-compose.yml config
```

To start all services in sequence:
```shell
docker-compose -f deployment/docker-compose.yml up -d
```

To stop all services:
```shell
docker-compose -f deployment/docker-compose.yml stop
```

To remove all containers:
```shell
docker-compose -f deployment/docker-compose.yml down
```

You can also append the name of a container to any of these commands to apply the command _only to that container_.

## References

- [Docker Compose Environment Variables](https://docs.docker.com/compose/environment-variables/)