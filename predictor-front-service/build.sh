#!/usr/bin/env bash
mvn clean package
docker build -t build-model:1.0.0 .
docker tag build-model:1.0.0 registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/build-model:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/build-model:1.0.0