FROM maven:3.5.4-jdk-8-alpine

ADD . spring-kafka
WORKDIR spring-kafka
RUN mvn clean install -DskipTests

ENTRYPOINT ["java", "-jar", "target/spring-kafka-consumer-0.0.1-SNAPSHOT.jar"]

#CMD ["tail", "-f", "/dev/null"]