#!/bin/bash

export PATH=$PATH:/usr/local/bin

cd "$HOME/Downloads/readygsm-server"

./gradlew bootJar -x test

docker compose -f deploy/compose.yml -f deploy/compose.stage.yml up -d --build app