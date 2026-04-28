#!/bin/bash

cd "$HOME/Downloads/readygsm-server"

git pull origin develop

docker compose up -d

./gradlew bootJar -x test

pkill -f "java -jar.*readygsm-server" || true

sleep 2

nohup java -jar build/libs/readygsm-server-*.jar > app.log 2>&1 &
