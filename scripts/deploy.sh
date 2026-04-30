#!/bin/bash

cd "$HOME/Downloads/readygsm-server"

git pull origin develop

./gradlew bootJar -x test

docker compose up -d --build app