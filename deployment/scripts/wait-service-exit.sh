#!/bin/sh

while ping -c 1 "$1" > /dev/null 2>&1;
do
    echo "$1 still running!";
    sleep 1s;
done

echo "$1 has finished!"