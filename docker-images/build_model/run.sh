#!/usr/bin/env bash
docker stop build_model_1
docker rm build_model_1

docker run -d \
--name="build_model_1" \
-v /Users/xujun/Project/java_project/audio-deep-flow/docker-images/build_model/model.conf:/app/model/model.conf \
-v /Users/xujun/Project/python_project/tf-test/data/level:/app/data \
-v /Users/xujun/Project/java_project/audio-deep-flow/docker-images/build_model/save:/app/model/save \
build_model_launcher:1.0
