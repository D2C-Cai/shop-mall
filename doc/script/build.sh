#!/bin/bash

cd /data

git clone git://github.com/D2C-Cai/shop.git

cd ./shop

mvn clean install -Dmaven.test.skip=true dockerfile:build

docker run --net=host --name shop --env PROFILE=-Dspring.profiles.active=test -v /etc/localtime:/etc/localtime:ro -v /data/shop/target:/run -d d2c/shop

echo "OK"