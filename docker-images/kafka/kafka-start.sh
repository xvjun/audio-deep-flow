#!/usr/bin/env bash
docker stop xujun-kafka
docker rm xujun-kafka
cur_dir=$1
docker run  -d --name xujun-kafka \
-p 9092:9092 \
-e KAFKA_BROKER_ID=0 \
-e KAFKA_ZOOKEEPER_CONNECT=${cur_dir}:2181 \
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://${cur_dir}:9092 \
-e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -t wurstmeister/kafka:2.12-2.4.0