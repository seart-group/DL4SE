#!/usr/bin/env bash

(
cd dl4se-server/src/main/resources || exit;
keytool -genkeypair -alias dl4se \
        -keyalg RSA -keysize 4096 \
        -storetype PKCS12 -validity 365 \
        -keystore dl4se.p12 -storepass "$1"
)
