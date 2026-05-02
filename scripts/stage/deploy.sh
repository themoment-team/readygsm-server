#!/bin/bash

cd "$HOME/Downloads/readygsm-server"

git pull origin develop

./gradlew bootJar -x test

docker compose -f deploy/compose.yml -f deploy/compose.stage.yml up -d --build app