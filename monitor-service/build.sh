#!/usr/bin/env bash
mvn clean package
docker build -t monitor-service:1.0.0 .
docker tag monitor-service:1.0.0 registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/monitor-service:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/monitor-service:1.0.0