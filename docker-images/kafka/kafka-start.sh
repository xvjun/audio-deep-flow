#!/usr/bin/env bash
docker stop xujun-kafka
docker rm xujun-kafka

docker run  -d --name xujun-kafka \
-p 9092:9092 \
-e KAFKA_BROKER_ID=0 \
-e KAFKA_ZOOKEEPER_CONNECT=192.168.0.104:2181 \
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.0.104:9092 \
-e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -t wurstmeister/kafka:2.12-2.4.0