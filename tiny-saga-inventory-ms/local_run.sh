#!/bin/bash

# Download Maven Wrapper
mvn -N io.takari:maven:0.7.7:wrapper

# Run Maven clean install
./mvnw clean install

# Navigate to the target directory
cd target

# Find the JAR file
jar_file=$(ls *.jar 2>/dev/null)

if [ -z "$jar_file" ]; then
  echo "Error: JAR file not found in the target directory."
  exit 1
fi

# Run the JAR file
java -jar "$jar_file"
