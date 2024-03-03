#!/bin/bash

# Function to check if a Docker container exists for a given image name
container_exists() {
    local container_name="$1"
    if docker ps -a --format '{{.Names}}' | grep -q "^${container_name}$"; then
        return 0 # Container exists
    else
        return 1 # Container does not exist
    fi
}

# Function to delete a Docker container if it exists
delete_container() {
    local container_name="$1"
    if container_exists "$container_name"; then
        echo "Deleting existing container: $container_name"
        docker rm -f "$container_name"
    else
        echo "Container not found: $container_name"
    fi
}

# Function to run a Docker container
run_container() {
    local container_name="$1"
    local image_name="$2"
    local port_mapping="$3"
    echo "Creating container: $container_name"
    docker run -itd -p "$port_mapping" --name "$container_name" "$image_name"
}

# Run wallet ms
delete_container "wallet_ms"
run_container "wallet_ms" "tiny-saga-wallet-ms" "8086:8086"

# Run payment ms
delete_container "payment_ms"
run_container "payment_ms" "tiny-saga-payment-ms" "8087:8087"

# Run inventory ms
delete_container "inventory_ms"
run_container "inventory_ms" "tiny-saga-inventory-ms" "8088:8088"

# Run order ms
delete_container "order_ms"
run_container "order_ms" "tiny-saga-order-ms" "8081:8081"

# Run order ms
delete_container "orchestrator_ms"
run_container "orchestrator_ms" "tiny-saga-orchestrator-ms" "8084:8084"
