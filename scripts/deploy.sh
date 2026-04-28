#!/bin/bash

cd /Users/snowykte0426/Downloads/readygsm-server

git pull origin develop

docker compose up -d

pkill -f "readygsm-server" || true

sleep 2

./gradlew bootJar -x test

nohup java -jar build/libs/readygsm-server-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
