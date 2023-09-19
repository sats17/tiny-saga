#!/bin/bash

# Set the topic name and other configuration
TOPIC_NAME="order-topic"
PARTITIONS=1
REPLICATION_FACTOR=1

# Define the JSON event data
JSON_DATA='
{"eventId":"550e8400-e29b-41d4-a716-446655440000","correlationId":"8a2e2d59-9d36-4b87-8ae0-2a4eef15b7f6","eventName":"ORDER_INITIATED","version":"1.0","timestamp":214134323,"orderId":"12345","userId":"1","orderStatus":"INITIATED","paymentType":"WALLET","productId":"123asf-sfa-2a","productQuantity":2,"price":5000}

# Enter into kafka docker cluster
# Run kafka-console-producer command with one line json to send data
# kafka-console-producer --bootstrap-server kafka:9092 --topic order-topic

# Send the JSON event to Kafka
echo "$JSON_DATA" | docker run --rm -i \
  --network=infrastructure_default \
  confluentinc/cp-kafka \
  kafka-console-producer \
    --bootstrap-server kafka:9092 \
    --topic ${TOPIC_NAME} \
    --property parse.key=true \
    --property key.separator=,

echo "JSON event sent to Kafka topic: ${TOPIC_NAME}"
