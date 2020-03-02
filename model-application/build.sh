#!/usr/bin/env bash
mvn clean package
docker build -t model-app:1.0.0 .
docker tag model-app:1.0.0 registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/model-app:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/model-app:1.0.0