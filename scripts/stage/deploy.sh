#!/bin/bash

cd "$HOME/Downloads/readygsm-server"

git pull origin develop

./gradlew bootJar -x test

docker compose -f compose.yml -f compose.stage.yml up -d --build app