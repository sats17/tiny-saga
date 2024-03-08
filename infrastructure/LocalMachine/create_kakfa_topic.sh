#!/bin/bash

# Note: Script is not working with kafka installed on windows/linux machine. Use manual commands to create order topic.
# Set the topic name and other configuration
TOPIC_NAME="order-topic"
PARTITIONS=1
REPLICATION_FACTOR=1

# Check if the topic already exists
# It creates a new container which uses kafka network and uses kafka host to connect kafka cluster.
kafka-topics.sh --describe \
    --bootstrap-server kafka:9092 \
    --topic ${TOPIC_NAME} > /dev/null 2>&1

# If the topic doesn't exist, create it
if [ $? -ne 0 ]; then
    kafka-topics.sh --create \
      --bootstrap-server kafka:9092 \
      --topic ${TOPIC_NAME} \
      --partitions ${PARTITIONS} \
      --replication-factor ${REPLICATION_FACTOR}
else
  echo "Topic ${TOPIC_NAME} already exists."
fi
