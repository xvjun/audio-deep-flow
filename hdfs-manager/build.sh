#!/usr/bin/env bash
mvn clean package
docker build -t hdfs-manager:1.0.0 .
docker tag hdfs-manager:1.0.0 registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/hdfs-manager:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/hdfs-manager:1.0.0