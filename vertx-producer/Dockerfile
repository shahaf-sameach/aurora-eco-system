FROM maven:3.5.4-jdk-8-alpine

WORKDIR opt
ADD . vertex-producer
WORKDIR vertex-producer
RUN mvn clean install -DskipTests

ENTRYPOINT ["java", "-jar", "target/vertex-kafka-producer-0.0.1-SNAPSHOT.jar"]