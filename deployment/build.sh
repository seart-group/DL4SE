#!/usr/bin/env bash

docker build -t "dl4se/dl4se-$1:latest" -f "deployment/$1/Dockerfile" .