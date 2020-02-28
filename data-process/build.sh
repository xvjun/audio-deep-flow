#!/usr/bin/env bash
mvn clean package
docker build -t data-process:1.0.0 .
docker tag data-process:1.0.0 registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/data-process:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/audio-deep-flow/data-process:1.0.0