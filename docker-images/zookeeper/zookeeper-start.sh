#!/usr/bin/env bash
cur_dir=`pwd`
docker stop xujun-zookeeper
docker rm xujun-zookeeper
docker run -d --name xujun-zookeeper \
-v ${cur_dir}/data:/data \
-p 2181:2181 \
zookeeper:3.5
