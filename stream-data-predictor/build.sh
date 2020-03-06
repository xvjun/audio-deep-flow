#!/usr/bin/env bash
mvn clean package
docker build -t stream-data-predictor:1.0.0 .
docker tag stream-data-predictor:1.0.0 registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/stream-data-predictor:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/stream-data-predictor:1.0.0