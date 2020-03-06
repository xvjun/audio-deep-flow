#!/usr/bin/env bash
mvn clean package
docker build -t predictor-front-service:1.0.0 .
docker tag predictor-front-service:1.0.0 registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/predictor-front-service:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/predictor-front-service:1.0.0