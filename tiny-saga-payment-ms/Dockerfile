# With builder fetching corretto image temporary to save storage
FROM amazoncorretto:17 as builder
RUN yum update -y
RUN yum install -y procps
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} microservice.jar

ENTRYPOINT ["sh", "-c", "java -XX:+HeapDumpOnOutOfMemoryError -jar microservice.jar"]
