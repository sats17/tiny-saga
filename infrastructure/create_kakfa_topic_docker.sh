#!/bin/bash

# Set the topic name and other configuration
TOPIC_NAME="orchestrator-topic"
PARTITIONS=1
REPLICATION_FACTOR=1

# Check if the topic already exists
# It creates a new container which uses kafka network and uses kafka host to connect kafka cluster.
docker run --rm -it \
  --network=dockerinfra_kafka-network \
  confluentinc/cp-kafka \
  kafka-topics --describe \
    --bootstrap-server kafka:9092 \
    --topic ${TOPIC_NAME} > /dev/null 2>&1

# If the topic doesn't exist, create it
if [ $? -ne 0 ]; then
  docker run --rm -it \
    --network=dockerinfra_kafka-network \
    confluentinc/cp-kafka \
    kafka-topics --create \
      --bootstrap-server kafka:9092 \
      --topic ${TOPIC_NAME} \
      --partitions ${PARTITIONS} \
      --replication-factor ${REPLICATION_FACTOR}
else
  echo "Topic ${TOPIC_NAME} already exists."
fi
