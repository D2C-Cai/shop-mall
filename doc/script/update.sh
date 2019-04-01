#!/bin/bash

CONTAINTER_ID=`docker ps | grep d2c/shop | awk '{print $1}'`

cd /data/shop

git pull

mvn clean install -Dmaven.test.skip=true

docker restart $CONTAINTER_ID

echo "OK"