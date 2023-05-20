#!/bin/bash

# Set the topic name and other configuration
TOPIC_NAME="order-topic"
PARTITIONS=1
REPLICATION_FACTOR=1

# Check if the topic already exists
docker run --rm -it \
  --network=tiny-saga_default \
  confluentinc/cp-kafka \
  kafka-topics --list \
    --bootstrap-server kafka:9092 | grep -Fxq "${TOPIC_NAME}"

if [ $? -eq 0 ]; then
  echo "Topic '${TOPIC_NAME}' already exists. Skipping topic creation."
else
  # Run the Kafka container and create the topic
  # Get network name using "docker network ls" command
  docker run --rm -it \
    --network=tiny-saga_default \
    confluentinc/cp-kafka \
    kafka-topics --create \
      --bootstrap-server kafka:9092 \
      --topic ${TOPIC_NAME} \
      --partitions ${PARTITIONS} \
      --replication-factor ${REPLICATION_FACTOR}
fi
