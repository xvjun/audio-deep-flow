#!/usr/bin/env bash

cd /opt/kafka
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic send-hdfsmanager

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic hdfsmanager-send-dataprocess

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic hdfsmanager-send-buildmodel

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic hdfsmanager-send-servingapp

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic default-send-predictor-front

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic default-receiver-predictor-front

bin/kafka-topics.sh --list --bootstrap-server localhost:9092

