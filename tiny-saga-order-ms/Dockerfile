FROM amazoncorretto:17 as builder
RUN yum update -y
RUN yum install -y procps
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} microservice.jar

ENV KAFKA_HOST=localhost:9092

ENTRYPOINT ["sh", "-c", "java -XX:+HeapDumpOnOutOfMemoryError -jar microservice.jar"]
