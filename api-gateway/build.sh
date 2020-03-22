#!/usr/bin/env bash
mvn clean package
docker build -t api-gateway:1.0.0 .
docker tag api-gateway:1.0.0 registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/api-gateway:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/api-gateway:1.0.0